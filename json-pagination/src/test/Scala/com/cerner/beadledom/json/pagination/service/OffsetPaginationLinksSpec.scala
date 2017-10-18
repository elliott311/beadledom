package com.cerner.beadledom.json.pagination.service

import com.cerner.beadledom.json.pagination.model.OffsetPaginatedList

import org.jboss.resteasy.spi.ResteasyUriInfo
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, FunSpec, Matchers}

import javax.ws.rs.WebApplicationException
import javax.ws.rs.core.UriInfo

import scala.collection.JavaConverters._

class OffsetPaginationLinksSpec
    extends FunSpec with BeforeAndAfterEach with Matchers with MockitoSugar {

  private val list = OffsetPaginatedList.builder()
      .items(List("a", "b", "c").asJava)
      .totalResults(3L)
      .build()

  describe("OffsetPaginationLinks") {
    describe("#create") {
      it("throws a WebApplicationException with 400 status when offset is not a number") {
        val uriInfo = mockUriInfo(queryParams = ("offset", "blue"), ("limit", "2"))

        val exception = intercept[WebApplicationException] {
          OffsetPaginationLinks.create(list, uriInfo, 0, 20, "offset", "limit")
        }
        exception.getResponse.getStatus shouldBe 400
      }

      it("throws a WebApplicationException with 400 status when limit is not a number") {
        val uriInfo = mockUriInfo(queryParams = ("offset", "0"), ("limit", "purple"))

        val exception = intercept[WebApplicationException] {
          OffsetPaginationLinks.create(list, uriInfo, 0, 20, "offset", "limit")
        }
        exception.getResponse.getStatus shouldBe 400
      }

      it("uses 0 as the default offset if not set") {
        val uriInfo = mockUriInfo(queryParams = ("offset", null), ("limit", "2"))
        val links = OffsetPaginationLinks.create(list, uriInfo, 0, 20, "offset", "limit")
        links.firstLink() should include("offset=0")
      }

      it("it uses the default value for limit if not set") {
        val uriInfo = mockUriInfo(queryParams = ("offset", "2"), ("limit", null))
        val links = OffsetPaginationLinks.create(list, uriInfo, 0, 7, "offset", "limit")
        links.firstLink() should include("limit=7")
      }
    }

    describe("with total results") {
      describe("with total results less than page limit") {
        val totalResultsList = OffsetPaginatedList.builder()
            .items(List("a", "b", "c", "d").asJava)
            .totalResults(4L)
            .build()
        val uriInfo = mockUriInfo(queryParams = ("offset", "0"), ("limit", "4"))
        val links = OffsetPaginationLinks
            .create(totalResultsList, uriInfo, 0, 20, "offset", "limit")

        describe("when on first page") {
          it("has a first link") {
            links.firstLink() shouldBe "example.com?offset=0&limit=4"
          }

          it("has a last link") {
            links.lastLink() shouldBe "example.com?offset=0&limit=4"
          }

          it("does not have a next link") {
            links.nextLink() shouldBe null
          }

          it("does not have a previous link") {
            links.prevLink() shouldBe null
          }
        }
      }

      describe("with total results more than page limit") {
        val totalResultsList = OffsetPaginatedList.builder()
            .items(List("a", "b", "c", "d", "e", "f", "g", "h").asJava)
            .totalResults(500L)
            .build()

        describe("when on first page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "0"), ("limit", "20"))
          val links = OffsetPaginationLinks
              .create(totalResultsList, uriInfo, 0, 20, "offset", "limit")

          it("has a first link") {
            links.firstLink() shouldBe "example.com?offset=0&limit=20"
          }

          it("has a last link") {
            links.lastLink() shouldBe "example.com?offset=480&limit=20"
          }

          it("has a next link") {
            links.nextLink() shouldBe "example.com?offset=20&limit=20"
          }

          it("does not have a previous link") {
            links.prevLink() shouldBe null
          }
        }

        describe("when on last page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "480"), ("limit", "20"))
          val links = OffsetPaginationLinks
              .create(totalResultsList, uriInfo, 480, 20, "offset", "limit")

          it("has a first link") {
            links.firstLink() shouldBe "example.com?offset=0&limit=20"
          }

          it("has a last link") {
            links.lastLink() shouldBe "example.com?offset=480&limit=20"
          }

          it("does not have a next link") {
            links.nextLink() shouldBe null
          }

          it("has a previous link") {
            links.prevLink() shouldBe "example.com?offset=460&limit=20"
          }
        }

        describe("when on middle page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "100"), ("limit", "20"))
          val links = OffsetPaginationLinks
              .create(totalResultsList, uriInfo, 100, 20, "offset", "limit")

          it("has a first link") {
            links.firstLink() shouldBe "example.com?offset=0&limit=20"
          }

          it("has a last link") {
            links.lastLink() shouldBe "example.com?offset=480&limit=20"
          }

          it("has a next link") {
            links.nextLink() shouldBe "example.com?offset=120&limit=20"
          }

          it("has a previous link") {
            links.prevLink() shouldBe "example.com?offset=80&limit=20"
          }
        }
      }

      describe("with total results not a multiple of limit") {
        val totalResultsList = OffsetPaginatedList.builder()
            .items(List("a", "b", "c", "d", "e", "f", "g", "h").asJava)
            .totalResults(510L)
            .build()

        describe("when on first page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "0"), ("limit", "20"))
          val links = OffsetPaginationLinks
              .create(totalResultsList, uriInfo, 0, 20, "offset", "limit")

          it("has a first link") {
            links.firstLink() shouldBe "example.com?offset=0&limit=20"
          }

          it("has a last link") {
            links.lastLink() shouldBe "example.com?offset=500&limit=20"
          }

          it("has a next link") {
            links.nextLink() shouldBe "example.com?offset=20&limit=20"
          }

          it("does not have a previous link") {
            links.prevLink() shouldBe null
          }
        }

        describe("when on last page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "500"), ("limit", "20"))
          val links = OffsetPaginationLinks
              .create(totalResultsList, uriInfo, 500, 20, "offset", "limit")

          it("has a first link") {
            links.firstLink() shouldBe "example.com?offset=0&limit=20"
          }

          it("has a last link") {
            links.lastLink() shouldBe "example.com?offset=500&limit=20"
          }

          it("does not have a next link") {
            links.nextLink() shouldBe null
          }

          it("has a previous link") {
            links.prevLink() shouldBe "example.com?offset=480&limit=20"
          }
        }

        describe("when on middle page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "100"), ("limit", "20"))
          val links = OffsetPaginationLinks
              .create(totalResultsList, uriInfo, 100, 20, "offset", "limit")

          it("has a first link") {
            links.firstLink() shouldBe "example.com?offset=0&limit=20"
          }

          it("has a last link") {
            links.lastLink() shouldBe "example.com?offset=500&limit=20"
          }

          it("has a next link") {
            links.nextLink() shouldBe "example.com?offset=120&limit=20"
          }

          it("has a previous link") {
            links.prevLink() shouldBe "example.com?offset=80&limit=20"
          }
        }
      }
    }

    describe("without total results") {
      describe("with has more true") {
        val totalResultsList = OffsetPaginatedList.builder()
            .items(List("a", "b", "c", "d").asJava)
            .hasMore(true)
            .build()

        describe("when on first page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "0"), ("limit", "20"))
          val links = OffsetPaginationLinks
              .create(totalResultsList, uriInfo, 0, 20, "offset", "limit")

          it("has a first link") {
            links.firstLink() shouldBe "example.com?offset=0&limit=20"
          }

          it("does not have a last link") {
            links.lastLink() shouldBe null
          }

          it("has a next link") {
            links.nextLink() shouldBe "example.com?offset=20&limit=20"
          }

          it("does not have a previous link") {
            links.prevLink() shouldBe null
          }
        }

        describe("when on middle page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "100"), ("limit", "20"))
          val links = OffsetPaginationLinks
              .create(totalResultsList, uriInfo, 100, 20, "offset", "limit")

          it("has a first link") {
            links.firstLink() shouldBe "example.com?offset=0&limit=20"
          }

          it("does not have a last link") {
            links.lastLink() shouldBe null
          }

          it("has a next link") {
            links.nextLink() shouldBe "example.com?offset=120&limit=20"
          }

          it("has a previous link") {
            links.prevLink() shouldBe "example.com?offset=80&limit=20"
          }
        }
      }

      describe("with has more false") {
        val totalResultsList = OffsetPaginatedList.builder()
            .items(List("a", "b", "c", "d").asJava)
            .hasMore(false)
            .build()

        describe("when on first page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "0"), ("limit", "20"))
          val links = OffsetPaginationLinks
              .create(totalResultsList, uriInfo, 0, 20, "offset", "limit")

          it("has a first link") {
            links.firstLink() shouldBe "example.com?offset=0&limit=20"
          }

          it("does not have a last link") {
            links.lastLink() shouldBe null
          }

          it("does not have a next link") {
            links.nextLink() shouldBe null
          }

          it("does not have a previous link") {
            links.prevLink() shouldBe null
          }
        }

        describe("when on middle page") {
          val uriInfo = mockUriInfo(queryParams = ("offset", "100"), ("limit", "20"))
          val links = OffsetPaginationLinks
              .create(totalResultsList, uriInfo, 100, 20, "offset", "limit")

          it("has a first link") {
            links.firstLink() shouldBe "example.com?offset=0&limit=20"
          }

          it("does not have a last link") {
            links.lastLink() shouldBe null
          }

          it("does not have a next link") {
            links.nextLink() shouldBe null
          }

          it("has a previous link") {
            links.prevLink() shouldBe "example.com?offset=80&limit=20"
          }
        }
      }
    }

    describe("when totalResults and hasMore contradict") {
      val list = OffsetPaginatedList.builder()
          .items(List("a", "b", "c", "d").asJava)
          .hasMore(false)
          .totalResults(100L)
          .build()
      val uriInfo = mockUriInfo()
      val links = OffsetPaginationLinks.create(list, uriInfo, 0, 20, "offset", "limit")

      it("has a first link") {
        links.firstLink() shouldBe "example.com?offset=0&limit=20"
      }

      it("has a last link") {
        links.lastLink() shouldBe "example.com?offset=80&limit=20"
      }

      it("has a next link") {
        links.nextLink() shouldBe "example.com?offset=20&limit=20"
      }

      it("does not have a previous link") {
        links.prevLink() shouldBe null
      }
    }
  }

  private def mockUriInfo(queryParams: (String, String)*): UriInfo = {
    val queryString = queryParams
        .filter({ case (_, v) => v != null })
        .map({ case (k, v) => s"$k=$v" }).mkString("&")
    val uriInfo = new ResteasyUriInfo("example.com", queryString, "")

    uriInfo
  }
}
