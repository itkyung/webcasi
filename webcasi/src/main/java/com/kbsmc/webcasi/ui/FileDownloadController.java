package com.kbsmc.webcasi.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kbsmc.webcasi.admin.IAdminHomeService;
import com.kbsmc.webcasi.common.CommonUtils;
import com.kbsmc.webcasi.common.MIMEUtil;
import com.kbsmc.webcasi.entity.HelpContents;

@Controller
public class FileDownloadController {
	
	@Autowired private IAdminHomeService adminService;
	
	private String commonUploadPath;
	
	@Value("#{defaultConfig['common.uploadPath']}")
	public void setFileUploadPath(String fileUploadPath) {
		this.commonUploadPath = fileUploadPath;
	}	
	
	@RequestMapping("/filedownload")
	public void downloadFile(
			HttpServletRequest req,
			HttpServletResponse res,
			@RequestParam(value="helpId", required = true) String helpId) throws Exception {
		if(helpId != null) {
			HelpContents help = adminService.loadHelpContents(helpId);
			File file = new File(commonUploadPath+help.getAttachFilePath());
			if(file.isFile()) {
				String fileName = file.getName().substring(file.getName().indexOf("_")+1);
				String encoding = req.getCharacterEncoding();
				encoding = encoding == null ? "EUC-KR" : encoding;
				fileName = URLEncoder.encode(fileName, encoding);
				res.setHeader("Pragma", "public");
				res.setContentLength((int)file.length());
				
				String mimeType = MIMEUtil.getMimeType(fileName);
				
				if(mimeType == null || !mimeType.startsWith("image")) {
					String strClient=req.getHeader("User-Agent");
					res.setContentType("application/x-msdownload");
					if(strClient.indexOf("MSIE 5.5")>-1) {
						res.setHeader("Accept-Ranges", "bytes");
						res.setHeader("Content-Disposition", "filename=\""+ fileName + "\";");
					} else {
						res.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\";");
						res.setHeader("Content-Transfer-Encoding", "binary;");
					}
				} else {
					res.setContentType(mimeType);
					res.setHeader("Accept-Ranges", "bytes");
					res.setHeader("Content-Transfer-Encoding", "binary;");
					res.setHeader("Content-Disposition", "attachment; filename=\""+fileName+"\";");
				}
				
				try {
					FileInputStream fileInputStream = new FileInputStream(file);
					CommonUtils.transfer(fileInputStream, res.getOutputStream(), false);
				} catch(IOException ex) {
					ex.printStackTrace();
				}
				
				res.flushBuffer();
			} else {
				res.setContentType("text/html");
				throw new IllegalArgumentException("File path is empty!");
			}
	    } else {
			res.setContentType("text/html");
			throw new IllegalArgumentException("File path is empty!");
	    }
	  }
}
