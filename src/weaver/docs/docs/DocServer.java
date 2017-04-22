package weaver.docs.docs;

import javax.servlet.*;
import javax.servlet.http.*;
import DBstep.iMsgServer2000;
import java.io.*;
import java.util.*;

import two.docs.docmould.DocMouldServer;
import weaver.general.*;
import weaver.hrm.HrmUserVarify;
import weaver.hrm.User;

/**
 * <p>Title: </p>
 * <p>Description: IWebOffice2000 插件 服务器端的类 封装了IMsgServer2000 和一些公用的操作
 * <p>如果有进一步的操作可以扩展这个类进行相应的操作</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: 泛微</p>
 * @author 王金永
 * @version 1.0
 */
public class DocServer extends BaseBean {
    private DocInfo docInfo ;   //存储传、递文档的数据信息
    private iMsgServer2000 msgObj ; //插件服务器端组件
    private HttpServletRequest request ;
    private HttpServletResponse response ;
    private User user;

    /**
     * 存储Servlet的请求和输出对象
     * @param request 由Servlet传入的一个HttpServletRequest对象
     * @param response 由Servlet传入的一个HttpServletResponse对象
     */
    public DocServer(HttpServletRequest request,HttpServletResponse response) {
        docInfo = new DocInfo();
        msgObj = new iMsgServer2000();
        user = HrmUserVarify.getUser (request , response) ;
        this.request = request;
        this.response = response;
    }

    /**
     * 在这里进行客户端插件的数据提取和服务器端的数据输出<p>
     * 根据客户端命令的不同调用相应的方法完成实际的数据存储过程（比如存到数据库或者文件系统）
     * @throws Exception
     */
    public void doCommand() throws Exception {
        String option = "";
        String command = "";
        try{
            String temStr = weaver.general.GCONST.getRootPath() +  "tempfile";
            docInfo.setFilePath(temStr) ; //如果保存为文件，则填写文件路径
            //docInfo.setUserName(request.getSession().getAttribute("UserName").toString()) ; //取得保存用户名称
            //docInfo.setUserName("测试人") ;
            // 只允许用POST方法提交的请求
            if(request.getMethod().equalsIgnoreCase("POST")) {
                //读取客户端插件提交的数据包
                //msgObj.MsgVariant(readPackage(request)) ;
            	msgObj.Load(request);
                // DBSTEP = ?
                if(msgObj.GetMsgByName("DBSTEP").equalsIgnoreCase("DBSTEP")) {
                    //获得操作的类型
                    option = msgObj.GetMsgByName("OPTION") ;

                    if(option.equalsIgnoreCase("LOADFILE")) {   //请求调用文档
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ;   //取得文档编号
                        docInfo.setFileName(msgObj.GetMsgByName("FILENAME"));    //取得文档名称
                        docInfo.setFileType(msgObj.GetMsgByName("FILETYPE"));    //取得文档类型

                        if(this.loadFile(docInfo,msgObj)) { //调入文档
                            //msgObj.MsgFileBody(docInfo.getFileBody()) ; //将文件信息打包
                            //msgObj.MsgFileSize(docInfo.getFileSize()) ;
                            msgObj.SetMsgByName("STATUS", "打开成功!") ; //设置状态信息
                            msgObj.MsgError("") ;         //清除错误信息
                        } else {
                            msgObj.MsgError("打开失败!") ; //设置错误信息
                        }
                     } else if(option.equalsIgnoreCase("SAVEFILE")) { //请求保存文档
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //取得文档编号
                        docInfo.setFileName(msgObj.GetMsgByName("FILENAME")) ; //取得文档名称
                        docInfo.setFileType(msgObj.GetMsgByName("FILETYPE")) ; //取得文档类型
                        if(!docInfo.getFileType().equals(".doc") 
                        		&& !docInfo.getFileType().equals(".wps")
                        		&& !docInfo.getFileType().equals(".ppt")
                        		&& !docInfo.getFileType().equals(".xls")
                        		&& !docInfo.getFileType().equals(".et")) {
                        	docInfo.setFileType(".doc");
                        }
                        docInfo.setFileSize(msgObj.MsgFileSize()) ; //取得文档大小
                        docInfo.setFileDate(docInfo.getCurTime()); //取得文档时间
                        docInfo.setFileBody(msgObj.MsgFileBody()) ; //取得文档内容
                        docInfo.setDescript("通用版本") ; //版本说明
                        //msgObj.MsgTextClear() ;
                        if(saveFile(docInfo,msgObj)) { //保存文档内容
                            msgObj.SetMsgByName("STATUS", "保存成功!") ; //设置状态信息
                            msgObj.MsgError("") ; //清除错误信息
                        } else {
                            msgObj.MsgError("保存失败!") ; //设置错误信息
                        }
                        msgObj.MsgFileClear() ;
                    } else if(option.equalsIgnoreCase("LOADTEMPLATE")) { //请求调用模板文档
                        docInfo.setTemplate(msgObj.GetMsgByName("TEMPLATE")) ; //取得模板编号
                        String mCommand = msgObj.GetMsgByName("COMMAND");
                        if (mCommand!=null&&mCommand.equalsIgnoreCase("LOADVIEWMOULD")) {
                            if(loadViewTemplate(docInfo, msgObj)) { //调入显示模板
                                msgObj.SetMsgByName("STATUS", "打开模板成功!") ; //设置状态信息
                                msgObj.MsgError("") ; //清除错误信息
                            } else {
                                msgObj.MsgError("打开模板失败!") ; //设置错误信息
                            }
                        } else {
	                        if(loadEditTemplate(docInfo, msgObj)) { //调入编辑模板
	                            msgObj.SetMsgByName("STATUS", "打开模板成功!") ; //设置状态信息
	                            msgObj.MsgError("") ; //清除错误信息
	                        } else {
	                            msgObj.MsgError("打开模板失败!") ; //设置错误信息
	                        }
                        }
                    } else if(option.equalsIgnoreCase("SAVETEMPLATE")) { //请求保存模板文档
                        docInfo.setTemplate(msgObj.GetMsgByName("TEMPLATE")) ;
                        docInfo.setFileName(msgObj.GetMsgByName("FILENAME")) ; //取得文档名称
                        docInfo.setFileType(msgObj.GetMsgByName("FILETYPE")) ; //取得文档类型
                        docInfo.setFileSize(msgObj.MsgFileSize()) ; //取得文档大小
                        docInfo.setFileDate(docInfo.getCurTime() ); //取得文档时间
                        docInfo.setFileBody(msgObj.MsgFileBody()) ; //取得文档内容
                        docInfo.setDescript("通用模板") ;
                        //msgObj.MsgTextClear() ;
                        if(saveTemplate(docInfo,msgObj)) { //保存模板文档内容
                            msgObj.SetMsgByName("STATUS", "保存模板成功!") ; //设置状态信息
                            msgObj.MsgError("") ; //清除错误信息
                        } else {
                            msgObj.MsgError("保存模板失败!") ; //设置错误信息
                        }
                        msgObj.MsgFileClear() ;
                    } else if(option.equalsIgnoreCase("LISTVERSION")) { //打开版本列表
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //取得文档编号
                        //mFileName=MsgObj.GetMsgByName("FILENAME");		//取得文档名称
                        //mFileType=MsgObj.GetMsgByName("FILETYPE");		//取得文档类型
                        //msgObj.MsgTextClear() ;
                        if(listVersion(docInfo,msgObj)) { //生成版本列表
                            msgObj.SetMsgByName("FILEID", docInfo.getFileID()) ; //将文档号列表打包
                            msgObj.SetMsgByName("DATETIME", docInfo.getDateTime()) ; //将日期时间列表打包
                            msgObj.SetMsgByName("USERNAME", docInfo.getUserName()) ; //将用户名列表打包
                            msgObj.SetMsgByName("DESCRIPT", docInfo.getDescript()) ; //将说明信息列表打包
                            msgObj.SetMsgByName("STATUS", "版本列表成功!") ; //设置状态信息
                            msgObj.MsgError("") ; //清除错误信息
                        } else {
                            msgObj.MsgError("版本列表失败!") ; //设置错误信息
                        }
                    } else if(option.equalsIgnoreCase("LOADVERSION")) { //打开版本文档
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //取得文档编号
                        //mFileName=MsgObj.GetMsgByName("FILENAME");		//取得文档名称
                        //mFileType=MsgObj.GetMsgByName("FILETYPE");		//取得文档类型
                        docInfo.setFileID(msgObj.GetMsgByName("FILEID")) ; //取得版本文档号
                        //msgObj.MsgTextClear() ;
                        if(loadVersion(docInfo,msgObj)) { //调入该版本文档
                            msgObj.MsgFileBody(docInfo.getFileBody()) ; //将文档信息打包
                            msgObj.SetMsgByName("STATUS", "打开版本成功!") ; //设置状态信息
                            msgObj.MsgError("") ; //清除错误信息
                        } else {
                            msgObj.MsgError("打开版本失败!") ; //设置错误信息
                        }
                    } else if(option.equalsIgnoreCase("SAVEVERSION")) { //保存版本文档
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //取得文档编号
                        docInfo.setFileName(msgObj.GetMsgByName("FILENAME")) ; //取得文档名称
                        docInfo.setFileType(msgObj.GetMsgByName("FILETYPE")) ; //取得文档类型
                        docInfo.setFileSize(msgObj.MsgFileSize()) ; //取得文档大小
                        docInfo.setFileDate(docInfo.getCurTime() ) ; //取得文档时间
                        docInfo.setFileBody(msgObj.MsgFileBody()) ; //取得文档内容
                        //docInfo.setDescript(msgObj.GetMsgByName("DESCRIPT")) ; //版本说明
                        docInfo.setDescript("测试版本") ;

                        //msgObj.MsgTextClear() ;
                        if(saveVersion(docInfo,msgObj)) { //保存版本文档
                            msgObj.SetMsgByName("STATUS", "保存版本成功!") ; //设置状态信息
                            msgObj.MsgError("") ; //清除错误信息
                        } else {
                            msgObj.MsgError("保存版本失败!") ; //设置错误信息
                        }
                        msgObj.MsgFileClear() ; //清除文档内容
                    } else if(option.equalsIgnoreCase("LOADBOOKMARKS")) { //设置标签文档内容
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //取得文档编号
                        docInfo.setTemplate(msgObj.GetMsgByName("TEMPLATE")) ; //取得模板编号
                        //msgObj.MsgTextClear() ;
                      //flazy增加如果是从表单进来的就调用系统的方法
                        if("1".equals(msgObj.GetMsgByName("ISFROMTABLE"))){
                        	DocMouldServer docMouldServer=new DocMouldServer();
                        	docMouldServer.loadBookMarks(docInfo, msgObj);
                        //结束
                        }else if(loadBookMarks(docInfo,msgObj)) {
                            HashMap bookMarks = docInfo.getBookMarks() ;
                            Iterator it = bookMarks.keySet().iterator() ;
                            while(it.hasNext()){
                                String name = String.valueOf(it.next()) ;
                                String value = String.valueOf(bookMarks.get(name)) ;
                                msgObj.SetMsgByName(name,value) ;
                                bookMarks.clear() ;
                            }
                            msgObj.MsgError("") ; //清除错误信息
                        } else {
                            msgObj.MsgError("打开标签信息失败!") ; //设置错误信息
                        }
                    } else if(option.equalsIgnoreCase("SAVEBOOKMARKS")) { //保存标签
                    	if(msgObj.GetMsgByName("FILETYPE").equals(".doc")||msgObj.GetMsgByName("FILETYPE").equals(".wps")) {
	                    	if(msgObj.GetMsgByName("SAVETYPE").equalsIgnoreCase("EDIT"))
	                        	docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //取得文档编号
	                    	else
	                    		docInfo.setRecordID(msgObj.GetMsgByName("CREATEID")) ; //取得文档编号
                        
//	                    	int starti = 0;
//	                    	int endi = 0;
//	                        for(int i = 0 ; i < msgObj.GetFieldCount() ; i++) {
//	                        	if(msgObj.GetFieldName(i).equalsIgnoreCase("USERNAME")){
//	                        		starti = i;
//	                        	}
//	                        	if(msgObj.GetFieldName(i).equalsIgnoreCase("LOCATION")){
//	                        		endi = i;
//	                        		break;
//	                        	}
//	                        }
//	                        
//	                        boolean hasContent = false;
//	                        for(int i=starti+1;i<endi;i++){
//	                            docInfo.getBookMarkName().add(msgObj.GetFieldName(i));
//	                            docInfo.getBookMarks().put(msgObj.GetFieldName(i),msgObj.GetFieldValue(i));
//	                            if(msgObj.GetFieldName(i).equals("Content")) hasContent = true;
//	                        }

	                    	String bookMarkNames=Util.null2String(msgObj.GetMsgByName("BOOKMARKNAMES"));
	                    	bookMarkNames=","+bookMarkNames+",";
	                        boolean hasContent = false;	                        
	                        for(int i=0;i<msgObj.GetFieldCount();i++){	                        	
	                        	if(bookMarkNames.indexOf(Util.null2String(msgObj.GetFieldName(i)))==-1){
	                        		continue;
	                        	}
	                            docInfo.getBookMarkName().add(msgObj.GetFieldName(i));
	                            docInfo.getBookMarks().put(msgObj.GetFieldName(i),msgObj.GetFieldValue(i));
	                            if(msgObj.GetFieldName(i).equals("Content")) hasContent = true;
	                        }	                        
	                        //必需设置正文标签
	                        if(!hasContent){
	                        	msgObj.MsgError("未设置正文标签Content!") ; //设置错误信息
	                        } else {
		                        if(saveBookMarks(docInfo,msgObj)) {
		                            msgObj.MsgError("") ; //清除错误信息
		                        } else {
		                            msgObj.MsgError("保存标签信息失败!") ; //设置错误信息
		                        }
		                        //msgObj.MsgTextClear() ;
	                        }
                    	}
                    } else if(option.equalsIgnoreCase("LISTBOOKMARKS")) { //取得标签
                        //msgObj.MsgTextClear() ;
                        if(this.listBookMarks(docInfo,msgObj)) {
                            msgObj.SetMsgByName("BOOKMARK", docInfo.getBookMark()) ; //将用户名列表打包
                            msgObj.SetMsgByName("DESCRIPT", docInfo.getDescript()) ; //将说明信息列表打包
                            msgObj.MsgError("") ; //清除错误信息
                        } else {
                            msgObj.MsgError("调入标签失败!") ; //设置错误信息
                        }
                    } else if(option.equalsIgnoreCase("LOADMARKLIST")) { //下面的代码为创建印章列表
                        docInfo.setUserName(msgObj.GetMsgByName("USERID")) ;
                        //msgObj.MsgTextClear() ;
                        if(loadMarkList(docInfo,msgObj)) {
                            msgObj.SetMsgByName("MARKLIST", docInfo.getMarkList() ) ;
                            msgObj.MsgError("") ; //清除错误信息
                        } else {
                            msgObj.MsgError("创建印章列表失败!") ; //设置错误信息
                        }
                    } else if(option.equalsIgnoreCase("LOADMARKIMAGE")) { //下面的代码为打开印章文件
                        docInfo.setMarkName(msgObj.GetMsgByName("IMAGENAME")) ; //取得文档名
                        docInfo.setUserName(msgObj.GetMsgByName("USERID")) ; //取得文档名
                        docInfo.setPassword(msgObj.GetMsgByName("PASSWORD")) ; //取得文档类型
                        docInfo.setFileType(".jpg") ; //默认为.jpg类型
                        //msgObj.MsgTextClear() ;
                        if(loadMarkImage(docInfo,msgObj)) { //调入文档
                            msgObj.SetMsgByName("IMAGETYPE", docInfo.getFileType() ) ; //设置图片类型
                            //msgObj.MsgFileBody(docInfo.getFileBody() ) ; //将文件信息打包
                            msgObj.MsgFileLoad(docInfo.getFilePath()) ;
                            msgObj.SetMsgByName("STATUS", "打开成功!") ; //设置状态信息
                            msgObj.MsgError("") ; //清除错误信息
                        } else {
                            msgObj.MsgError("签名或密码错误!") ; //设置错误信息
                        }
                    } else if(option.equalsIgnoreCase("LOADSIGNATURE")) { //下面的代码为打开印章文件
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //取得文档编号
                        //msgObj.MsgTextClear() ;
                        if(loadSignature(docInfo,msgObj)) { //调入文档
                            msgObj.SetMsgByName("MARKNAME", docInfo.getMarkName() ) ;
                            msgObj.SetMsgByName("USERNAME", docInfo.getUserName() ) ;
                            msgObj.SetMsgByName("DATETIME", docInfo.getDateTime() ) ;
                            msgObj.SetMsgByName("HOSTNAME", docInfo.getHostName() ) ;
                            msgObj.SetMsgByName("MARKGUID", docInfo.getMarkGuid()) ;
                            msgObj.SetMsgByName("STATUS", "调入成功!") ; //设置状态信息
                            msgObj.MsgError("") ; //清除错误信息
                        } else {
                            msgObj.MsgError("调入标签失败!") ; //设置错误信息
                        }
                    } else if(option.equalsIgnoreCase("SAVESIGNATURE")) { //下面的代码为保存印章文件
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //取得文档编号
                        //mTemplate=MsgObj.GetMsgByName("TEMPLATE");		//取得模板编号
                        docInfo.setFileName(msgObj.GetMsgByName("FILENAME")) ; //取得标签文档内容
                        docInfo.setMarkName(msgObj.GetMsgByName("MARKNAME")) ; //取得标签文档内容
                        docInfo.setUserName(msgObj.GetMsgByName("USERNAME")) ; //取得标签文档内容
                        docInfo.setDateTime(msgObj.GetMsgByName("DATETIME")) ; //取得标签文档内容
                        docInfo.setHostName(request.getRemoteAddr()) ; //Request.ServerVariables("REMOTE_ADDR")
                        docInfo.setMarkGuid(msgObj.GetMsgByName("MARKGUID")) ;
                        //msgObj.MsgTextClear() ;
                        if(saveSignature(docInfo,msgObj)) { //调入文档
                            msgObj.SetMsgByName("STATUS", "保存成功!") ; //设置状态信息
                            msgObj.MsgError("") ; //清除错误信息
                        } else {
                            msgObj.MsgError("保存标签失败!") ; //设置错误信息
                        }
                    } else if(option.equalsIgnoreCase("SAVEASHTML")) {
                        docInfo.setHtmlName(msgObj.GetMsgByName("HTMLNAME")) ; //取得标签文档内容
                        docInfo.setDirectory(msgObj.GetMsgByName("DIRECTORY")) ; //取得标签文档内容
                        //msgObj.MsgTextClear() ;
                        saveAsHtml(docInfo,msgObj);

                        msgObj.MakeDirectory(docInfo.getFilePath() ) ;

                        if(msgObj.MsgFileSave(docInfo.getFilePath() + "\\" + docInfo.getHtmlName() )) {
                            msgObj.MsgError("") ; //清除错误信息
                            msgObj.SetMsgByName("STATUS", "保存成功") ; //设置状态信息
                        } else {
                            msgObj.MsgError("保存失败") ; //设置错误信息
                        }
                        msgObj.MsgFileClear() ;
                    } else if(option.equalsIgnoreCase("INSERTFILE")) { //请求调用正文文档
                    	docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //取得文档编号
                        msgObj.MsgTextClear() ;
                        if(insertFile(docInfo,msgObj)) { //调入文档
                        	msgObj.SetMsgByName("POSITION","Content"); //设置插入的位置[书签对象名]
                            msgObj.SetMsgByName("STATUS", "插入文件成功!") ; //设置状态信息
                            msgObj.MsgError("") ; //清除错误信息
                        } else {
                            msgObj.MsgError("插入正文失败!") ; //设置错误信息
                        }
                    } else if(option.equalsIgnoreCase("UPDATEFILE")) { //请求保存 定搞文档
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //取得文档编号
                        docInfo.setFileName(msgObj.GetMsgByName("FILENAME")) ; //取得文档名称
                        docInfo.setFileType(msgObj.GetMsgByName("FILETYPE")) ; //取得文档类型
                        docInfo.setFileSize(msgObj.MsgFileSize()) ; //取得文档大小
                        docInfo.setFileDate(docInfo.getCurTime() ) ; //取得文档时间
                        docInfo.setFileBody(msgObj.MsgFileBody()) ; //取得文档内容
                        docInfo.setFilePath("") ; //如果保存为文件，则填写文件路径
                        //docInfo.setUserName(docInfo.getUserName() ); //取得保存用户名称
                        docInfo.setDescript("定稿版本") ; //版本说明
                        //msgObj.MsgTextClear() ;
                        if(updateFile(docInfo,msgObj)) { //保存文档内容
                            msgObj.SetMsgByName("STATUS", "保存成功!") ; //设置状态信息
                            msgObj.MsgError("") ; //清除错误信息
                        } else {
                            msgObj.MsgError("保存失败!") ; //设置错误信息
                        }
                        msgObj.MsgFileClear() ;
                    } else if(option.equalsIgnoreCase("SENDMESSAGE")) { //Web页面请求信息
                        sendMessage(docInfo,msgObj);
                    }
                }

            } else {
                msgObj.MsgError("请使用Post方法") ;
                msgObj.MsgTextClear() ;
                msgObj.MsgFileClear() ;
            }
            //SendPackage(response) ;
            msgObj.Send(response);               
        } catch(Exception ex) {
            //log
            ex.printStackTrace() ;
            throw ex;
        }
    }

    /**
     * 读取文档并把数据存储到DocInfo里
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean loadFile(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 保存DocInfo里的数据
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean saveFile(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 读取版本信息列表放到DocInfo里
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean listVersion(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 读取特定版本文件信息放到DocInfo里
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean loadVersion(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 保存DocInfo里的版本文件信息
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean saveVersion(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 读取模板信息存储到DocInfo里
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean loadTemplate(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 读取模板信息存储到DocInfo里
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean loadEditTemplate(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };
    
    /**
     * 读取模板信息存储到DocInfo里
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean loadViewTemplate(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };
    
    /**
     * 保存DocInfo里的模板信息
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean saveTemplate(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 读取书签信息放到DocInfo里
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean loadBookMarks(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 读取书签信息放到DocInfo里
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean listBookMarks(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 保存DocInfo里的书签信息
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean saveBookMarks(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 读取签名或印章列表到DocInfo里
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean loadMarkList(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 读取签名或印章到DocInfo里
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean loadMarkImage(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 保存DocInfo里的签名或印章信息
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean saveSignature(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 监测信息
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean loadSignature(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 保存DocInfo里的数据为html文件
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean saveAsHtml(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 调入fileName指定的文件，插入到指定位置
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean insertFile(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 保存fileName指定的文件
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean updateFile(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     *
     * @param docInfo 负责数据的传递
     * @return true/false 代表操作 成功/失败
     */
    protected boolean sendMessage(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * 取得客户端发来的数据包
     * @param request HttpServletRequest
     * @return 客户端的数据包
     * @throws Exception
     */
    private byte[] readPackage(HttpServletRequest request) throws Exception {
        byte stream[] = null ;
        int totalRead = 0 ;
        int readBytes = 0 ;
        int totalBytes = 0 ;
        try {
            totalBytes = request.getContentLength() ;
            stream = new byte[totalBytes] ;
            while(totalRead < totalBytes) {
                request.getInputStream() ;
                readBytes = request.getInputStream().read(stream, totalRead
                    , totalBytes - totalRead) ;
                totalRead += readBytes ;
                continue ;
            }
        } catch(Exception ex) {
            //log
            throw ex;
        }
        return(stream) ;
    }

    /**
     * 发送处理后的数据包
     * @param response @throws Exception
     * @throws Exception
     */
    private void SendPackage(HttpServletResponse response) throws Exception {
    	ServletOutputStream outBinarry =null;
        try {
            //ServletOutputStream outBinarry = response.getOutputStream() ;
            outBinarry = response.getOutputStream() ;       	
            outBinarry.write(msgObj.MsgVariant()) ;
            outBinarry.flush() ;
            //outBinarry.close() ;
        } catch(Exception ex) {
            //log
            throw ex;
        } finally{
        	try{
        		outBinarry.close() ;
        	}catch(Exception ex){
        		
        	}
        }
    }

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}





















}