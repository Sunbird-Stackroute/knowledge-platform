package controllers

import akka.actor.{ActorRef, ActorSystem}
import handlers.SignalHandler
import org.sunbird.meeting.constant.MeetingConstants
import play.api.mvc._
import utils.{ActorNames, ApiId}

import javax.inject._
import scala.collection.JavaConverters.mapAsJavaMapConverter
import scala.concurrent.ExecutionContext

class MeetingsController @Inject()(@Named(ActorNames.MEETING_ACTOR) meetingActor: ActorRef, cc: ControllerComponents, actorSystem: ActorSystem, signalHandler: SignalHandler)(implicit exec: ExecutionContext) extends BaseController(cc) {
    
    final val DEFAULT_VERSION = "1.0"
    
    def create(): Action[AnyContent] = Action.async { implicit request =>
        val meetingRequest = requestBody()
        val headers = commonHeaders()
        meetingRequest.putAll(headers)
        val createRequest = getRequest(meetingRequest, headers, MeetingConstants.CREATE_MEETING)
        getResult(ApiId.CREATE_MEETING, meetingActor, createRequest, categoryMapping = false, DEFAULT_VERSION)
    }
    
    def get(meetingID: String): Action[AnyContent] = Action.async { implicit request =>
        val headers = commonHeaders()
        val meetingRequest = new java.util.HashMap().asInstanceOf[java.util.Map[String, Object]]
        meetingRequest.putAll(headers)
        meetingRequest.putAll(Map(MeetingConstants.MEETING_ID -> meetingID).asJava)
        val readRequest = getRequest(meetingRequest, headers, MeetingConstants.GET_MEETING)
        getResult(ApiId.GET_MEETING, meetingActor, readRequest, categoryMapping = false, DEFAULT_VERSION)
    }
    
    def generateSignature(): Action[AnyContent] = Action.async { implicit request =>
        val meetingRequest = requestBody()
        val headers = commonHeaders()
        meetingRequest.putAll(headers)
        val generateRequest = getRequest(meetingRequest, headers, MeetingConstants.GENERATE_MEETING_SIGNATURE)
        getResult(ApiId.GENERATE_MEETING_SIGNATURE, meetingActor, generateRequest, categoryMapping = false, DEFAULT_VERSION)
    }
    
}
