package org.sunbird.meeting.actors

import org.sunbird.actor.core.BaseActor
import org.sunbird.cloudstore.StorageService
import org.sunbird.common.{HttpUtil, MeetingUtil, Platform}
import org.sunbird.common.dto.{Request, Response}
import org.sunbird.graph.OntologyEngineContext
import org.sunbird.meeting.constant.MeetingConstants

import java.util
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class MeetingActor @Inject()(implicit oec: OntologyEngineContext, ss: StorageService) extends BaseActor {
    
    implicit val ec: ExecutionContext = getContext().dispatcher
    
    override def onReceive(request: Request): Future[Response] = {
        request.getOperation match {
            case MeetingConstants.CREATE_MEETING => create(request)
            case MeetingConstants.GET_MEETING => read(request)
            case MeetingConstants.GENERATE_MEETING_SIGNATURE => generateSignature(request)
        }
    }
    
    def create(request: Request): Future[Response] = {
        val url = Platform.getString("meeting.api.endpoint", "") + "v2/users/hritikm46@gmail.com/meetings";
        val body: util.Map[String, Object] = request.getRequest
        val headers: util.Map[String, String] = new util.HashMap()
        headers.put("content-type", "application/json")
        headers.put("authorization", "Bearer " + getToken)
        val httpUtil = new HttpUtil()
        Future(httpUtil.postForObject(url, body, headers))
    }
    
    def read(request: Request): Future[Response] = {
        val url = Platform.getString("meeting.api.endpoint", "") + "v2/meetings/" + request.get("meetingId");
        val headers: util.Map[String, String] = new util.HashMap()
        headers.put("authorization", "Bearer " + getToken)
        val httpUtil = new HttpUtil()
        val resp = httpUtil.getForObject(url, "", headers)
        Future(resp)
    }
    
    def generateSignature(request: Request): Future[Response] = {
        val apiKey = Platform.getString("meeting.api.key", "");
        val apiSecret = Platform.getString("meeting.api.secret", "");
        val meetingNumber = request.get("meetingID").toString
        val role = request.get("role").asInstanceOf[Int]
        val signature = MeetingUtil.generateSignature(apiKey, apiSecret, meetingNumber, role)
        val response = new Response()
        response.put("signature", signature)
        Future(response)
    }
    
    def getToken: String = {
        val apiKey = Platform.getString("meeting.api.key", "");
        val apiSecret = Platform.getString("meeting.api.secret", "");
        MeetingUtil.getToken(apiKey, apiSecret)
    }
}
