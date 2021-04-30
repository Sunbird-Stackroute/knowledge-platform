package org.sunbird.meeting.actors

import org.sunbird.actor.core.BaseActor
import org.sunbird.cloudstore.StorageService
import org.sunbird.common.HttpUtil
import org.sunbird.common.dto.{Request, Response}
import org.sunbird.common.exception.ResponseCode
import org.sunbird.graph.OntologyEngineContext
import org.sunbird.meeting.constant.MeetingConstants
import org.sunbird.meeting.db.MeetingStore
import org.sunbird.meeting.util.{MeetingUtil, MeetingValidator}

import java.util
import java.util.Date
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MeetingActor @Inject()(implicit oec: OntologyEngineContext, ss: StorageService) extends BaseActor {
    
    implicit val ec: ExecutionContext = getContext().dispatcher
    
    final val meetingStore = new MeetingStore()
    
    override def onReceive(request: Request): Future[Response] = {
        request.getOperation match {
            case MeetingConstants.CREATE_MEETING => create(request)
            case MeetingConstants.GET_MEETING => read(request)
            case MeetingConstants.GENERATE_MEETING_SIGNATURE => generateSignature(request)
        }
    }
    
    def create(request: Request): Future[Response] = {
        val httpUtil = new HttpUtil()
        val resp = httpUtil.postForObject(MeetingConstants.CREATE_API_ENDPOINT, request.getRequest, MeetingConstants.getApiHeaders)
        if (resp.getResponseCode.equals(ResponseCode.CREATED)) {
            val data: util.Map[String, Object] = new util.HashMap(resp.getResult)
            data.put(MeetingConstants.COLUMNS.CREATED_BY, request.get(MeetingConstants.COLUMNS.CREATED_BY))
            data.put(MeetingConstants.COLUMNS.CONTENT_ID, request.get(MeetingConstants.COLUMNS.CONTENT_ID))
            data.put(MeetingConstants.COLUMNS.UPDATED_AT, new Date())
            meetingStore.insertMeeting(data)
            setMeetingResponse(resp)
        }
        Future(resp)
    }
    
    def read(request: Request): Future[Response] = {
        val url = MeetingConstants.GET_MEETING_API_ENDPOINT + request.get(MeetingConstants.MEETING_ID)
        val httpUtil = new HttpUtil()
        val resp = httpUtil.getForObject(url, MeetingConstants.getApiHeaders)
        if (resp.getResponseCode.equals(ResponseCode.OK)) {
            setMeetingResponse(resp)
        }
        Future(resp)
    }
    
    private def setMeetingResponse(response: Response): Unit = {
        val processedData: util.Map[String, Object] = new util.HashMap(MeetingUtil.filterDataForResponse(response.getResult))
        response.getResult.clear()
        response.putAll(processedData)
    }
    
    def generateSignature(request: Request): Future[Response] = {
        MeetingValidator.validateRequest(request.getRequest, util.Arrays.asList(MeetingConstants.MEETING_ID, MeetingConstants.ROLE))
        val meetingNumber = request.get(MeetingConstants.MEETING_ID).toString
        val role = request.get(MeetingConstants.ROLE).asInstanceOf[Int]
        val signature = MeetingUtil.generateSignature(MeetingConstants.API_KEY, MeetingConstants.API_SECRET, meetingNumber, role)
        val response = new Response()
        response.put(MeetingConstants.SIGNATURE, signature)
        Future(response)
    }
}
