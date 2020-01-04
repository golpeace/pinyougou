package com.pinyougou.sms;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

@RestController
@RequestMapping("/sms")
public class SmsController {
	
	private   String product = "Dysmsapi";
    //产品域名,开发者无需替换
	private  String domain = "dysmsapi.aliyuncs.com";
	@Value("${accessKeyId}")
	private   String accessKeyId;
	@Value("${accessKeySecret}")
	private   String accessKeySecret;
//	Code=OK
//			Message=OK
//			RequestId=CB45F91A-D6FB-4C61-9550-2BB1892E35F1
//			BizId=421117633713719892^0
	
//	phoneNumbers电话号码
//	signName 签名
//	templateCode 模板编码.
//	templateParam 模板中参数
	@RequestMapping(value="/sendSms")
	public Map sendSms(String phoneNumbers,String signName ,String templateCode,String  templateParam) throws Exception {
		//可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");

        //初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        IAcsClient acsClient = new DefaultAcsClient(profile);

        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号  
        request.setPhoneNumbers(phoneNumbers);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
//        您的验证码为：${code}，该验证码 5 分钟内有效，请勿泄漏于他人。
        System.out.println(templateParam);
        request.setTemplateParam(templateParam);

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");

        //hint 此处可能会抛出异常，注意catch
        SendSmsResponse response = acsClient.getAcsResponse(request);
        
        Map map = new HashMap();
        
        map.put("code", response.getCode());
        map.put("message", response.getMessage());
        map.put("requestId", response.getRequestId());
        map.put("bizId", response.getBizId());
        
        return map;
	}

}
