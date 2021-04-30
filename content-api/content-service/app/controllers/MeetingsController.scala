package controllers

import akka.actor.{ActorRef, ActorSystem}
import handlers.SignalHandler
import org.sunbird.common.exception.ClientException
import org.sunbird.meeting.constant.MeetingConstants
import org.sunbird.meeting.util.MeetingValidator
import play.api.mvc._
import utils.{ActorNames, ApiId}

import java.util
import javax.inject._
import scala.collection.JavaConverters.mapAsJavaMapConverter
import scala.concurrent.{ExecutionContext, Future}

class MeetingsController @Inject()(@Named(ActorNames.MEETING_ACTOR) meetingActor: ActorRef, cc: ControllerComponents, actorSystem: ActorSystem, signalHandler: SignalHandler)(implicit exec: ExecutionContext) extends BaseController(cc) {
    
    final val DEFAULT_VERSION = "1.0"
    
    def create(): Action[AnyContent] = Action.async { implicit request =>
        val meetingRequest = requestBody()
        val headers = commonHeaders()
        meetingRequest.putAll(headers)
        val createRequest = getRequest(meetingRequest, headers, MeetingConstants.CREATE_MEETING)
        getValidResult(createRequest, ApiId.CREATE_MEETING, util.Arrays.asList(MeetingConstants.COLUMNS.CREATED_BY,
            MeetingConstants.COLUMNS.TOPIC,
            MeetingConstants.COLUMNS.TYPE,
            MeetingConstants.COLUMNS.SETTINGS), DEFAULT_VERSION)
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
        getValidResult(generateRequest, ApiId.GENERATE_MEETING_SIGNATURE, util.Arrays.asList(MeetingConstants.MEETING_ID, MeetingConstants.ROLE), DEFAULT_VERSION)
    }
    
    def getValidResult(request: org.sunbird.common.dto.Request, apiId:String, validationFields:util.List[String], version:String): Future[Result] = {
        try {
            MeetingValidator.validateRequest(request.getRequest, validationFields)
            getResult(apiId, meetingActor, request, categoryMapping = false, version)
        } catch {
            case ce:ClientException => getErrorResponse(apiId, version, ce.getResponseCode.name(), ce.getMessage)
        }
    }
    
}
