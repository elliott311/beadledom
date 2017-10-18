package com.cerner.beadledom.json.pagination.service

import com.cerner.beadledom.json.pagination.model.{OffsetPaginatedList, OffsetPaginatedListDto}

import org.jboss.resteasy.core.interception.AbstractWriterInterceptorContext
import org.jboss.resteasy.spi.ResteasyUriInfo
import org.mockito.Mockito.when
import org.mockito.ArgumentMatchers.any
import org.scalatest.mock.MockitoSugar
import org.scalatest.{FunSpec, Matchers}

import javax.ws.rs.container.ResourceInfo
import javax.ws.rs.core.UriInfo

import scala.collection.JavaConverters._

class OffsetPaginationListWriterInterceptorSpec extends FunSpec with Matchers with MockitoSugar {
  val resourceInfo = mock[ResourceInfo]
  val methodWithDefaultNames = Class
      .forName("com.cerner.beadledom.json.pagination.service.OffsetPaginationLinkTestMethods")
      .getDeclaredMethod("paginatedListDefaults", Class.forName("java.lang.String"),
        Class.forName("java.lang.Integer"), Class.forName("java.lang.Integer"))
  val methodWithOverrideOffset = Class
      .forName("com.cerner.beadledom.json.pagination.service.OffsetPaginationLinkTestMethods")
      .getDeclaredMethod("paginatedListOverrideOffset", Class.forName("java.lang.String"),
        Class.forName("java.lang.Integer"), Class.forName("java.lang.Integer"))
  val methodWithOverrideLimit = Class
      .forName("com.cerner.beadledom.json.pagination.service.OffsetPaginationLinkTestMethods")
      .getDeclaredMethod("paginatedListOverrideLimit", Class.forName("java.lang.String"),
        Class.forName("java.lang.Integer"), Class.forName("java.lang.Integer"))

  describe("OffsetPaginatedListLinksWriterInterceptor") {
    describe("#aroundWriteTo") {
      it("replaces an OffsetPaginatedList entity with an OffsetPaginatedListDto entity") {
        val list = OffsetPaginatedList.builder()
            .items(List("a", "b").asJava)
            .hasMore(true)
            .build()
        val uriInfo = mockUriInfo()

        val interceptor = new OffsetPaginatedListWriterInterceptor
        interceptor.uriInfo = uriInfo
        interceptor.resourceInfo=resourceInfo

        when(resourceInfo.getResourceMethod).thenReturn(methodWithDefaultNames)
        val context = mock[AbstractWriterInterceptorContext]
        when(context.setEntity(any())).thenCallRealMethod()
        when(context.getEntity).thenCallRealMethod()

        context.setEntity(list)
        interceptor.aroundWriteTo(context)
        val listWithLinks = context.getEntity.asInstanceOf[OffsetPaginatedListDto[String]]

        listWithLinks.items.asScala shouldBe List("a", "b")
        listWithLinks.firstLink() shouldBe "example.com?offset=0&limit=20"
        listWithLinks.lastLink() shouldBe null
        listWithLinks.nextLink() shouldBe "example.com?offset=20&limit=20"
        listWithLinks.prevLink() shouldBe null
      }

      it("does not replace entities of other types") {
        val uriInfo = mockUriInfo()

        val interceptor = new OffsetPaginatedListWriterInterceptor
        interceptor.uriInfo = uriInfo

        val context = mock[AbstractWriterInterceptorContext]
        when(context.setEntity(any())).thenCallRealMethod()
        when(context.getEntity).thenCallRealMethod()

        val entity = List("a", "b", "c").asJava
        context.setEntity(entity)
        interceptor.aroundWriteTo(context)

        context.getEntity shouldBe entity
      }
      it("identifies an overridden offset parameter name and uses it.") {
        val list = OffsetPaginatedList.builder()
            .items(List("a", "b").asJava)
            .hasMore(true)
            .build()
        val uriInfo = mockUriInfo()

        val interceptor = new OffsetPaginatedListWriterInterceptor
        interceptor.uriInfo = uriInfo
        interceptor.resourceInfo=resourceInfo

        when(resourceInfo.getResourceMethod).thenReturn(methodWithOverrideOffset)
        val context = mock[AbstractWriterInterceptorContext]
        when(context.setEntity(any())).thenCallRealMethod()
        when(context.getEntity).thenCallRealMethod()

        context.setEntity(list)
        interceptor.aroundWriteTo(context)
        val listWithLinks = context.getEntity.asInstanceOf[OffsetPaginatedListDto[String]]

        listWithLinks.items.asScala shouldBe List("a", "b")
        listWithLinks.firstLink() shouldBe "example.com?start=0&limit=20"
        listWithLinks.lastLink() shouldBe null
        listWithLinks.nextLink() shouldBe "example.com?start=20&limit=20"
        listWithLinks.prevLink() shouldBe null
      }

      it("identifies an overridden limit parameter name and uses it.") {
        val list = OffsetPaginatedList.builder()
            .items(List("a", "b").asJava)
            .hasMore(true)
            .build()
        val uriInfo = mockUriInfo()

        val interceptor = new OffsetPaginatedListWriterInterceptor
        interceptor.uriInfo = uriInfo
        interceptor.resourceInfo=resourceInfo

        when(resourceInfo.getResourceMethod).thenReturn(methodWithOverrideLimit)
        val context = mock[AbstractWriterInterceptorContext]
        when(context.setEntity(any())).thenCallRealMethod()
        when(context.getEntity).thenCallRealMethod()

        context.setEntity(list)
        interceptor.aroundWriteTo(context)
        val listWithLinks = context.getEntity.asInstanceOf[OffsetPaginatedListDto[String]]

        listWithLinks.items.asScala shouldBe List("a", "b")
        listWithLinks.firstLink() shouldBe "example.com?offset=0&pagesize=20"
        listWithLinks.lastLink() shouldBe null
        listWithLinks.nextLink() shouldBe "example.com?offset=20&pagesize=20"
        listWithLinks.prevLink() shouldBe null
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
