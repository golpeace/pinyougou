package com.pinyougou.manager.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import util.FastDFSClient;

@RestController
public class UploadController  {
	
	@Value("${file_server_ip}")
	private String file_server_ip;
	
	@RequestMapping("/upload")
	public Result uploadFile(MultipartFile file) {
		try {
			FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
			
			String originalFilename = file.getOriginalFilename(); //io.wu.mi6.jpg   png  jpeg  gif
			
			String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
			
			String uploadFile = fastDFSClient.uploadFile(file.getBytes(), extName);
//			group1/M00/00/00/wKgZhVtYT26ADH18AACuI4TeyLI104.jpg
			
			return new Result(true, file_server_ip+uploadFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new Result(false, "上传失败");
		}
		
	}

}
