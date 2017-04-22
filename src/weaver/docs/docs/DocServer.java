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
 * <p>Description: IWebOffice2000 ��� �������˵��� ��װ��IMsgServer2000 ��һЩ���õĲ���
 * <p>����н�һ���Ĳ���������չ����������Ӧ�Ĳ���</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ��΢</p>
 * @author ������
 * @version 1.0
 */
public class DocServer extends BaseBean {
    private DocInfo docInfo ;   //�洢�������ĵ���������Ϣ
    private iMsgServer2000 msgObj ; //��������������
    private HttpServletRequest request ;
    private HttpServletResponse response ;
    private User user;

    /**
     * �洢Servlet��������������
     * @param request ��Servlet�����һ��HttpServletRequest����
     * @param response ��Servlet�����һ��HttpServletResponse����
     */
    public DocServer(HttpServletRequest request,HttpServletResponse response) {
        docInfo = new DocInfo();
        msgObj = new iMsgServer2000();
        user = HrmUserVarify.getUser (request , response) ;
        this.request = request;
        this.response = response;
    }

    /**
     * ��������пͻ��˲����������ȡ�ͷ������˵��������<p>
     * ���ݿͻ�������Ĳ�ͬ������Ӧ�ķ������ʵ�ʵ����ݴ洢���̣�����浽���ݿ�����ļ�ϵͳ��
     * @throws Exception
     */
    public void doCommand() throws Exception {
        String option = "";
        String command = "";
        try{
            String temStr = weaver.general.GCONST.getRootPath() +  "tempfile";
            docInfo.setFilePath(temStr) ; //�������Ϊ�ļ�������д�ļ�·��
            //docInfo.setUserName(request.getSession().getAttribute("UserName").toString()) ; //ȡ�ñ����û�����
            //docInfo.setUserName("������") ;
            // ֻ������POST�����ύ������
            if(request.getMethod().equalsIgnoreCase("POST")) {
                //��ȡ�ͻ��˲���ύ�����ݰ�
                //msgObj.MsgVariant(readPackage(request)) ;
            	msgObj.Load(request);
                // DBSTEP = ?
                if(msgObj.GetMsgByName("DBSTEP").equalsIgnoreCase("DBSTEP")) {
                    //��ò���������
                    option = msgObj.GetMsgByName("OPTION") ;

                    if(option.equalsIgnoreCase("LOADFILE")) {   //��������ĵ�
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ;   //ȡ���ĵ����
                        docInfo.setFileName(msgObj.GetMsgByName("FILENAME"));    //ȡ���ĵ�����
                        docInfo.setFileType(msgObj.GetMsgByName("FILETYPE"));    //ȡ���ĵ�����

                        if(this.loadFile(docInfo,msgObj)) { //�����ĵ�
                            //msgObj.MsgFileBody(docInfo.getFileBody()) ; //���ļ���Ϣ���
                            //msgObj.MsgFileSize(docInfo.getFileSize()) ;
                            msgObj.SetMsgByName("STATUS", "�򿪳ɹ�!") ; //����״̬��Ϣ
                            msgObj.MsgError("") ;         //���������Ϣ
                        } else {
                            msgObj.MsgError("��ʧ��!") ; //���ô�����Ϣ
                        }
                     } else if(option.equalsIgnoreCase("SAVEFILE")) { //���󱣴��ĵ�
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //ȡ���ĵ����
                        docInfo.setFileName(msgObj.GetMsgByName("FILENAME")) ; //ȡ���ĵ�����
                        docInfo.setFileType(msgObj.GetMsgByName("FILETYPE")) ; //ȡ���ĵ�����
                        if(!docInfo.getFileType().equals(".doc") 
                        		&& !docInfo.getFileType().equals(".wps")
                        		&& !docInfo.getFileType().equals(".ppt")
                        		&& !docInfo.getFileType().equals(".xls")
                        		&& !docInfo.getFileType().equals(".et")) {
                        	docInfo.setFileType(".doc");
                        }
                        docInfo.setFileSize(msgObj.MsgFileSize()) ; //ȡ���ĵ���С
                        docInfo.setFileDate(docInfo.getCurTime()); //ȡ���ĵ�ʱ��
                        docInfo.setFileBody(msgObj.MsgFileBody()) ; //ȡ���ĵ�����
                        docInfo.setDescript("ͨ�ð汾") ; //�汾˵��
                        //msgObj.MsgTextClear() ;
                        if(saveFile(docInfo,msgObj)) { //�����ĵ�����
                            msgObj.SetMsgByName("STATUS", "����ɹ�!") ; //����״̬��Ϣ
                            msgObj.MsgError("") ; //���������Ϣ
                        } else {
                            msgObj.MsgError("����ʧ��!") ; //���ô�����Ϣ
                        }
                        msgObj.MsgFileClear() ;
                    } else if(option.equalsIgnoreCase("LOADTEMPLATE")) { //�������ģ���ĵ�
                        docInfo.setTemplate(msgObj.GetMsgByName("TEMPLATE")) ; //ȡ��ģ����
                        String mCommand = msgObj.GetMsgByName("COMMAND");
                        if (mCommand!=null&&mCommand.equalsIgnoreCase("LOADVIEWMOULD")) {
                            if(loadViewTemplate(docInfo, msgObj)) { //������ʾģ��
                                msgObj.SetMsgByName("STATUS", "��ģ��ɹ�!") ; //����״̬��Ϣ
                                msgObj.MsgError("") ; //���������Ϣ
                            } else {
                                msgObj.MsgError("��ģ��ʧ��!") ; //���ô�����Ϣ
                            }
                        } else {
	                        if(loadEditTemplate(docInfo, msgObj)) { //����༭ģ��
	                            msgObj.SetMsgByName("STATUS", "��ģ��ɹ�!") ; //����״̬��Ϣ
	                            msgObj.MsgError("") ; //���������Ϣ
	                        } else {
	                            msgObj.MsgError("��ģ��ʧ��!") ; //���ô�����Ϣ
	                        }
                        }
                    } else if(option.equalsIgnoreCase("SAVETEMPLATE")) { //���󱣴�ģ���ĵ�
                        docInfo.setTemplate(msgObj.GetMsgByName("TEMPLATE")) ;
                        docInfo.setFileName(msgObj.GetMsgByName("FILENAME")) ; //ȡ���ĵ�����
                        docInfo.setFileType(msgObj.GetMsgByName("FILETYPE")) ; //ȡ���ĵ�����
                        docInfo.setFileSize(msgObj.MsgFileSize()) ; //ȡ���ĵ���С
                        docInfo.setFileDate(docInfo.getCurTime() ); //ȡ���ĵ�ʱ��
                        docInfo.setFileBody(msgObj.MsgFileBody()) ; //ȡ���ĵ�����
                        docInfo.setDescript("ͨ��ģ��") ;
                        //msgObj.MsgTextClear() ;
                        if(saveTemplate(docInfo,msgObj)) { //����ģ���ĵ�����
                            msgObj.SetMsgByName("STATUS", "����ģ��ɹ�!") ; //����״̬��Ϣ
                            msgObj.MsgError("") ; //���������Ϣ
                        } else {
                            msgObj.MsgError("����ģ��ʧ��!") ; //���ô�����Ϣ
                        }
                        msgObj.MsgFileClear() ;
                    } else if(option.equalsIgnoreCase("LISTVERSION")) { //�򿪰汾�б�
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //ȡ���ĵ����
                        //mFileName=MsgObj.GetMsgByName("FILENAME");		//ȡ���ĵ�����
                        //mFileType=MsgObj.GetMsgByName("FILETYPE");		//ȡ���ĵ�����
                        //msgObj.MsgTextClear() ;
                        if(listVersion(docInfo,msgObj)) { //���ɰ汾�б�
                            msgObj.SetMsgByName("FILEID", docInfo.getFileID()) ; //���ĵ����б���
                            msgObj.SetMsgByName("DATETIME", docInfo.getDateTime()) ; //������ʱ���б���
                            msgObj.SetMsgByName("USERNAME", docInfo.getUserName()) ; //���û����б���
                            msgObj.SetMsgByName("DESCRIPT", docInfo.getDescript()) ; //��˵����Ϣ�б���
                            msgObj.SetMsgByName("STATUS", "�汾�б�ɹ�!") ; //����״̬��Ϣ
                            msgObj.MsgError("") ; //���������Ϣ
                        } else {
                            msgObj.MsgError("�汾�б�ʧ��!") ; //���ô�����Ϣ
                        }
                    } else if(option.equalsIgnoreCase("LOADVERSION")) { //�򿪰汾�ĵ�
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //ȡ���ĵ����
                        //mFileName=MsgObj.GetMsgByName("FILENAME");		//ȡ���ĵ�����
                        //mFileType=MsgObj.GetMsgByName("FILETYPE");		//ȡ���ĵ�����
                        docInfo.setFileID(msgObj.GetMsgByName("FILEID")) ; //ȡ�ð汾�ĵ���
                        //msgObj.MsgTextClear() ;
                        if(loadVersion(docInfo,msgObj)) { //����ð汾�ĵ�
                            msgObj.MsgFileBody(docInfo.getFileBody()) ; //���ĵ���Ϣ���
                            msgObj.SetMsgByName("STATUS", "�򿪰汾�ɹ�!") ; //����״̬��Ϣ
                            msgObj.MsgError("") ; //���������Ϣ
                        } else {
                            msgObj.MsgError("�򿪰汾ʧ��!") ; //���ô�����Ϣ
                        }
                    } else if(option.equalsIgnoreCase("SAVEVERSION")) { //����汾�ĵ�
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //ȡ���ĵ����
                        docInfo.setFileName(msgObj.GetMsgByName("FILENAME")) ; //ȡ���ĵ�����
                        docInfo.setFileType(msgObj.GetMsgByName("FILETYPE")) ; //ȡ���ĵ�����
                        docInfo.setFileSize(msgObj.MsgFileSize()) ; //ȡ���ĵ���С
                        docInfo.setFileDate(docInfo.getCurTime() ) ; //ȡ���ĵ�ʱ��
                        docInfo.setFileBody(msgObj.MsgFileBody()) ; //ȡ���ĵ�����
                        //docInfo.setDescript(msgObj.GetMsgByName("DESCRIPT")) ; //�汾˵��
                        docInfo.setDescript("���԰汾") ;

                        //msgObj.MsgTextClear() ;
                        if(saveVersion(docInfo,msgObj)) { //����汾�ĵ�
                            msgObj.SetMsgByName("STATUS", "����汾�ɹ�!") ; //����״̬��Ϣ
                            msgObj.MsgError("") ; //���������Ϣ
                        } else {
                            msgObj.MsgError("����汾ʧ��!") ; //���ô�����Ϣ
                        }
                        msgObj.MsgFileClear() ; //����ĵ�����
                    } else if(option.equalsIgnoreCase("LOADBOOKMARKS")) { //���ñ�ǩ�ĵ�����
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //ȡ���ĵ����
                        docInfo.setTemplate(msgObj.GetMsgByName("TEMPLATE")) ; //ȡ��ģ����
                        //msgObj.MsgTextClear() ;
                      //flazy��������Ǵӱ������ľ͵���ϵͳ�ķ���
                        if("1".equals(msgObj.GetMsgByName("ISFROMTABLE"))){
                        	DocMouldServer docMouldServer=new DocMouldServer();
                        	docMouldServer.loadBookMarks(docInfo, msgObj);
                        //����
                        }else if(loadBookMarks(docInfo,msgObj)) {
                            HashMap bookMarks = docInfo.getBookMarks() ;
                            Iterator it = bookMarks.keySet().iterator() ;
                            while(it.hasNext()){
                                String name = String.valueOf(it.next()) ;
                                String value = String.valueOf(bookMarks.get(name)) ;
                                msgObj.SetMsgByName(name,value) ;
                                bookMarks.clear() ;
                            }
                            msgObj.MsgError("") ; //���������Ϣ
                        } else {
                            msgObj.MsgError("�򿪱�ǩ��Ϣʧ��!") ; //���ô�����Ϣ
                        }
                    } else if(option.equalsIgnoreCase("SAVEBOOKMARKS")) { //�����ǩ
                    	if(msgObj.GetMsgByName("FILETYPE").equals(".doc")||msgObj.GetMsgByName("FILETYPE").equals(".wps")) {
	                    	if(msgObj.GetMsgByName("SAVETYPE").equalsIgnoreCase("EDIT"))
	                        	docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //ȡ���ĵ����
	                    	else
	                    		docInfo.setRecordID(msgObj.GetMsgByName("CREATEID")) ; //ȡ���ĵ����
                        
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
	                        //�����������ı�ǩ
	                        if(!hasContent){
	                        	msgObj.MsgError("δ�������ı�ǩContent!") ; //���ô�����Ϣ
	                        } else {
		                        if(saveBookMarks(docInfo,msgObj)) {
		                            msgObj.MsgError("") ; //���������Ϣ
		                        } else {
		                            msgObj.MsgError("�����ǩ��Ϣʧ��!") ; //���ô�����Ϣ
		                        }
		                        //msgObj.MsgTextClear() ;
	                        }
                    	}
                    } else if(option.equalsIgnoreCase("LISTBOOKMARKS")) { //ȡ�ñ�ǩ
                        //msgObj.MsgTextClear() ;
                        if(this.listBookMarks(docInfo,msgObj)) {
                            msgObj.SetMsgByName("BOOKMARK", docInfo.getBookMark()) ; //���û����б���
                            msgObj.SetMsgByName("DESCRIPT", docInfo.getDescript()) ; //��˵����Ϣ�б���
                            msgObj.MsgError("") ; //���������Ϣ
                        } else {
                            msgObj.MsgError("�����ǩʧ��!") ; //���ô�����Ϣ
                        }
                    } else if(option.equalsIgnoreCase("LOADMARKLIST")) { //����Ĵ���Ϊ����ӡ���б�
                        docInfo.setUserName(msgObj.GetMsgByName("USERID")) ;
                        //msgObj.MsgTextClear() ;
                        if(loadMarkList(docInfo,msgObj)) {
                            msgObj.SetMsgByName("MARKLIST", docInfo.getMarkList() ) ;
                            msgObj.MsgError("") ; //���������Ϣ
                        } else {
                            msgObj.MsgError("����ӡ���б�ʧ��!") ; //���ô�����Ϣ
                        }
                    } else if(option.equalsIgnoreCase("LOADMARKIMAGE")) { //����Ĵ���Ϊ��ӡ���ļ�
                        docInfo.setMarkName(msgObj.GetMsgByName("IMAGENAME")) ; //ȡ���ĵ���
                        docInfo.setUserName(msgObj.GetMsgByName("USERID")) ; //ȡ���ĵ���
                        docInfo.setPassword(msgObj.GetMsgByName("PASSWORD")) ; //ȡ���ĵ�����
                        docInfo.setFileType(".jpg") ; //Ĭ��Ϊ.jpg����
                        //msgObj.MsgTextClear() ;
                        if(loadMarkImage(docInfo,msgObj)) { //�����ĵ�
                            msgObj.SetMsgByName("IMAGETYPE", docInfo.getFileType() ) ; //����ͼƬ����
                            //msgObj.MsgFileBody(docInfo.getFileBody() ) ; //���ļ���Ϣ���
                            msgObj.MsgFileLoad(docInfo.getFilePath()) ;
                            msgObj.SetMsgByName("STATUS", "�򿪳ɹ�!") ; //����״̬��Ϣ
                            msgObj.MsgError("") ; //���������Ϣ
                        } else {
                            msgObj.MsgError("ǩ�����������!") ; //���ô�����Ϣ
                        }
                    } else if(option.equalsIgnoreCase("LOADSIGNATURE")) { //����Ĵ���Ϊ��ӡ���ļ�
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //ȡ���ĵ����
                        //msgObj.MsgTextClear() ;
                        if(loadSignature(docInfo,msgObj)) { //�����ĵ�
                            msgObj.SetMsgByName("MARKNAME", docInfo.getMarkName() ) ;
                            msgObj.SetMsgByName("USERNAME", docInfo.getUserName() ) ;
                            msgObj.SetMsgByName("DATETIME", docInfo.getDateTime() ) ;
                            msgObj.SetMsgByName("HOSTNAME", docInfo.getHostName() ) ;
                            msgObj.SetMsgByName("MARKGUID", docInfo.getMarkGuid()) ;
                            msgObj.SetMsgByName("STATUS", "����ɹ�!") ; //����״̬��Ϣ
                            msgObj.MsgError("") ; //���������Ϣ
                        } else {
                            msgObj.MsgError("�����ǩʧ��!") ; //���ô�����Ϣ
                        }
                    } else if(option.equalsIgnoreCase("SAVESIGNATURE")) { //����Ĵ���Ϊ����ӡ���ļ�
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //ȡ���ĵ����
                        //mTemplate=MsgObj.GetMsgByName("TEMPLATE");		//ȡ��ģ����
                        docInfo.setFileName(msgObj.GetMsgByName("FILENAME")) ; //ȡ�ñ�ǩ�ĵ�����
                        docInfo.setMarkName(msgObj.GetMsgByName("MARKNAME")) ; //ȡ�ñ�ǩ�ĵ�����
                        docInfo.setUserName(msgObj.GetMsgByName("USERNAME")) ; //ȡ�ñ�ǩ�ĵ�����
                        docInfo.setDateTime(msgObj.GetMsgByName("DATETIME")) ; //ȡ�ñ�ǩ�ĵ�����
                        docInfo.setHostName(request.getRemoteAddr()) ; //Request.ServerVariables("REMOTE_ADDR")
                        docInfo.setMarkGuid(msgObj.GetMsgByName("MARKGUID")) ;
                        //msgObj.MsgTextClear() ;
                        if(saveSignature(docInfo,msgObj)) { //�����ĵ�
                            msgObj.SetMsgByName("STATUS", "����ɹ�!") ; //����״̬��Ϣ
                            msgObj.MsgError("") ; //���������Ϣ
                        } else {
                            msgObj.MsgError("�����ǩʧ��!") ; //���ô�����Ϣ
                        }
                    } else if(option.equalsIgnoreCase("SAVEASHTML")) {
                        docInfo.setHtmlName(msgObj.GetMsgByName("HTMLNAME")) ; //ȡ�ñ�ǩ�ĵ�����
                        docInfo.setDirectory(msgObj.GetMsgByName("DIRECTORY")) ; //ȡ�ñ�ǩ�ĵ�����
                        //msgObj.MsgTextClear() ;
                        saveAsHtml(docInfo,msgObj);

                        msgObj.MakeDirectory(docInfo.getFilePath() ) ;

                        if(msgObj.MsgFileSave(docInfo.getFilePath() + "\\" + docInfo.getHtmlName() )) {
                            msgObj.MsgError("") ; //���������Ϣ
                            msgObj.SetMsgByName("STATUS", "����ɹ�") ; //����״̬��Ϣ
                        } else {
                            msgObj.MsgError("����ʧ��") ; //���ô�����Ϣ
                        }
                        msgObj.MsgFileClear() ;
                    } else if(option.equalsIgnoreCase("INSERTFILE")) { //������������ĵ�
                    	docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //ȡ���ĵ����
                        msgObj.MsgTextClear() ;
                        if(insertFile(docInfo,msgObj)) { //�����ĵ�
                        	msgObj.SetMsgByName("POSITION","Content"); //���ò����λ��[��ǩ������]
                            msgObj.SetMsgByName("STATUS", "�����ļ��ɹ�!") ; //����״̬��Ϣ
                            msgObj.MsgError("") ; //���������Ϣ
                        } else {
                            msgObj.MsgError("��������ʧ��!") ; //���ô�����Ϣ
                        }
                    } else if(option.equalsIgnoreCase("UPDATEFILE")) { //���󱣴� �����ĵ�
                        docInfo.setRecordID(msgObj.GetMsgByName("RECORDID")) ; //ȡ���ĵ����
                        docInfo.setFileName(msgObj.GetMsgByName("FILENAME")) ; //ȡ���ĵ�����
                        docInfo.setFileType(msgObj.GetMsgByName("FILETYPE")) ; //ȡ���ĵ�����
                        docInfo.setFileSize(msgObj.MsgFileSize()) ; //ȡ���ĵ���С
                        docInfo.setFileDate(docInfo.getCurTime() ) ; //ȡ���ĵ�ʱ��
                        docInfo.setFileBody(msgObj.MsgFileBody()) ; //ȡ���ĵ�����
                        docInfo.setFilePath("") ; //�������Ϊ�ļ�������д�ļ�·��
                        //docInfo.setUserName(docInfo.getUserName() ); //ȡ�ñ����û�����
                        docInfo.setDescript("����汾") ; //�汾˵��
                        //msgObj.MsgTextClear() ;
                        if(updateFile(docInfo,msgObj)) { //�����ĵ�����
                            msgObj.SetMsgByName("STATUS", "����ɹ�!") ; //����״̬��Ϣ
                            msgObj.MsgError("") ; //���������Ϣ
                        } else {
                            msgObj.MsgError("����ʧ��!") ; //���ô�����Ϣ
                        }
                        msgObj.MsgFileClear() ;
                    } else if(option.equalsIgnoreCase("SENDMESSAGE")) { //Webҳ��������Ϣ
                        sendMessage(docInfo,msgObj);
                    }
                }

            } else {
                msgObj.MsgError("��ʹ��Post����") ;
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
     * ��ȡ�ĵ��������ݴ洢��DocInfo��
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean loadFile(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * ����DocInfo�������
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean saveFile(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * ��ȡ�汾��Ϣ�б�ŵ�DocInfo��
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean listVersion(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * ��ȡ�ض��汾�ļ���Ϣ�ŵ�DocInfo��
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean loadVersion(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * ����DocInfo��İ汾�ļ���Ϣ
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean saveVersion(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * ��ȡģ����Ϣ�洢��DocInfo��
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean loadTemplate(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * ��ȡģ����Ϣ�洢��DocInfo��
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean loadEditTemplate(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };
    
    /**
     * ��ȡģ����Ϣ�洢��DocInfo��
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean loadViewTemplate(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };
    
    /**
     * ����DocInfo���ģ����Ϣ
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean saveTemplate(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * ��ȡ��ǩ��Ϣ�ŵ�DocInfo��
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean loadBookMarks(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * ��ȡ��ǩ��Ϣ�ŵ�DocInfo��
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean listBookMarks(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * ����DocInfo�����ǩ��Ϣ
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean saveBookMarks(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * ��ȡǩ����ӡ���б�DocInfo��
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean loadMarkList(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * ��ȡǩ����ӡ�µ�DocInfo��
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean loadMarkImage(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * ����DocInfo���ǩ����ӡ����Ϣ
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean saveSignature(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * �����Ϣ
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean loadSignature(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * ����DocInfo�������Ϊhtml�ļ�
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean saveAsHtml(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * ����fileNameָ�����ļ������뵽ָ��λ��
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean insertFile(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * ����fileNameָ�����ļ�
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean updateFile(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     *
     * @param docInfo �������ݵĴ���
     * @return true/false ������� �ɹ�/ʧ��
     */
    protected boolean sendMessage(DocInfo docInfo,iMsgServer2000 msgObj){
        return false;
    };

    /**
     * ȡ�ÿͻ��˷��������ݰ�
     * @param request HttpServletRequest
     * @return �ͻ��˵����ݰ�
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
     * ���ʹ��������ݰ�
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