package weaver.docs.docs;

//import java.sql.* ;
//import java.text.*;
import java.io.*;

import DBstep.*;

//import java.io.* ;
import java.util.* ;
import java.util.zip.*;

import weaver.conn.* ;
import weaver.crm.Maint.CustomerInfoComInfo;
//import weaver.file.* ;
import weaver.file.FileManage;
import weaver.file.FileUpload;
import weaver.general.* ;
import weaver.hrm.resource.ResourceComInfo;
//import weaver.hrm.contract.ContractTypeComInfo ;
//import javax.servlet.* ;
import javax.servlet.http.* ;
//import java.net.* ;
import weaver.systeminfo.* ;
import weaver.systeminfo.language.LanguageComInfo;
import weaver.system.SystemComInfo;
//import java.sql.Timestamp ;
//import weaver.docs.* ;
import weaver.docs.bookmark.MouldBookMarkComInfo;
import weaver.docs.category.DocTreeDocFieldComInfo;
import weaver.docs.category.MainCategoryComInfo;
import weaver.docs.category.SecCategoryComInfo;
import weaver.docs.category.SecCategoryDocPropertiesComInfo;
import weaver.docs.category.SecCategoryMouldComInfo;
import weaver.docs.category.SubCategoryComInfo;

import weaver.docs.mould.DocMouldComInfo;
import weaver.docs.mould.MouldManager;
//import weaver.docs.mouldfile.* ;
//import oracle.sql.* ;
//import weaver.hrm.resource.* ;
//import weaver.hrm.User ;

import weaver.hrm.company.DepartmentComInfo;

/**
 * <p>Title: </p>
 * <p>Description: IWebOffice2000 插件 服务器端的类 封装了IMsgServer2000 和一些公用的操作，这个类扩展了DocServer</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: 泛微</p>
 * @author 王金永
 * @version 1.0
 */
public class DocDbServer extends DocServer {
	
	private static ImageFileIdUpdate imageFileIdUpdate = new ImageFileIdUpdate();
	
    public DocDbServer(HttpServletRequest request, HttpServletResponse response) {
        super(request, response) ;

    }


    protected boolean sendMessage(DocInfo docInfo, iMsgServer2000 msgObj) {
    	String command = msgObj.GetMsgByName("COMMAND");
    	if (command.equalsIgnoreCase("COPIES")){				//打印限制\

            int mCopies=Util.getIntValue(msgObj.GetMsgByName("OFFICEPRINTS"),0);//获得客户需要打印的份数
            int userId=Util.getIntValue(msgObj.GetMsgByName("USERID"),0);//用户id
            int docId=Util.getIntValue(msgObj.GetMsgByName("DOCID"),0);//文档id
            String clientAddress=Util.null2String(msgObj.GetMsgByName("CLIENTADDRESS"));//客户端ip

            String hasPrintNode=Util.null2String(msgObj.GetMsgByName("HASPRINTNODE"));
            String isPrintNode=Util.null2String(msgObj.GetMsgByName("ISPRINTNODE"));
            
            boolean canDoPrintByWorkflow=true;//是否可通过流程的打印节点打印
            if(isPrintNode.equals("false")){
            	canDoPrintByWorkflow=false;
            }
            
            int maxPrints=0;
            
            int sumPrintNum=0;
            int sumHasPrintNum=0;
            int canPrintedNum=0;
            int hasPrintedNum=0;
            
            RecordSet rs=new RecordSet();
            
        	StringBuffer sb=new StringBuffer();
        	sb.append(" select sum(printNum) as sumPrintNum,sum(hasPrintNum) as sumHasPrintNum    ")
        	  .append(" from workflow_requestbase a,Bill_DocPrintApply b ")
        	  .append(" where a.requestId=b.requestid ")
        	  .append("   and a.currentNodeType='3' ")
        	  .append("   and b.resourceId=").append(userId)
        	  .append("   and b.relatedDocId=").append(docId)
        	  .append("   and printNum>hasPrintNum ")
            ;
        	rs.executeSql(sb.toString()); 
        	if(rs.next()){
        		sumPrintNum=Util.getIntValue(rs.getString("sumPrintNum"),0);
        		sumHasPrintNum=Util.getIntValue(rs.getString("sumHasPrintNum"),0);        		
        	}
            
        	maxPrints=maxPrints+sumPrintNum-sumHasPrintNum;
        	
        	if(canDoPrintByWorkflow){
            	rs.executeSql("select canPrintedNum,hasPrintedNum from DocDetail where id="+docId+" and canPrintedNum>hasPrintedNum"); 
            	if(rs.next()){
            		canPrintedNum=Util.getIntValue(rs.getString("canPrintedNum"),0);
            		hasPrintedNum=Util.getIntValue(rs.getString("hasPrintedNum"),0);        		
            	}       	
            	maxPrints=maxPrints+canPrintedNum-hasPrintedNum;         		
        	}
        	
            if (mCopies<=maxPrints){
            	DocPrintLog docPrintLog=new DocPrintLog();
            	docPrintLog.setPrintUserId(userId);
            	docPrintLog.setPrintDocId(docId);
            	docPrintLog.setPrintNum(mCopies);
            	docPrintLog.setClientAddress(clientAddress);           	
            	docPrintLog.setDocPrintLogInfo();
            	
            	int tempId=0;
            	int tempPrintNum=0;
            	int tempHasPrintNum=0;
            	RecordSet rsSecond=new RecordSet();
            	
            	sb=new StringBuffer();
            	sb.append(" select b.id,b.printNum,b.hasPrintNum    ")
            	  .append(" from workflow_requestbase a,Bill_DocPrintApply b ")
            	  .append(" where a.requestId=b.requestid ")
            	  .append("   and a.currentNodeType='3' ")
            	  .append("   and b.resourceId=").append(userId)
            	  .append("   and b.relatedDocId=").append(docId)
            	  .append("   and printNum>hasPrintNum ")
            	  .append("   order by b.id asc ")
                ;
            	rs.executeSql(sb.toString());            	
            	while(rs.next()&&mCopies>0){
            		tempId=Util.getIntValue(rs.getString("id"),0);
            		tempPrintNum=Util.getIntValue(rs.getString("printNum"),0);
            		tempHasPrintNum=Util.getIntValue(rs.getString("hasPrintNum"),0);
            		
            		if(mCopies>tempPrintNum-tempHasPrintNum){
            			mCopies=mCopies-(tempPrintNum-tempHasPrintNum);
            			rs.executeSql("update Bill_DocPrintApply set hasPrintNum="+tempPrintNum+"  where id="+tempId);
            		}else{
            			rs.executeSql("update Bill_DocPrintApply set hasPrintNum=hasPrintNum+"+mCopies+"  where id="+tempId);          			
            			mCopies=0;
            		}
            	}
            	
            	if(mCopies>0){
        			rs.executeSql("update DocDetail set hasPrintedNum=hasPrintedNum+"+mCopies+"  where id="+docId);             		
            		mCopies=0;
            	}
            	
            	msgObj.SetMsgByName("STATUS", "1");						//设置状态信息，允许打印
            	msgObj.MsgError("");									//清除错误信息
            }else{
            	msgObj.SetMsgByName("MAXPRINTS", ""+maxPrints);			           	
            	msgObj.SetMsgByName("STATUS", "0");						//不允许打印
            	msgObj.MsgError("");				                    //清除错误信息
            }
        }    	
        return false ;
    }

    /**
     * 编码的转换 由数据库道页面
     * @param in 要转化的字符串
     * @return 转化后的字符串
     */
    private String DBtoPage(String in){
        try {
            if(in != null) {
                return new String(in.getBytes("ISO8859_1"), "GBK") ;
            } else {
                return "" ;
            }
        } catch(Exception ex) {
            return "";
        }
    }

    /**
     * 编码的转换 由页面到数据库
     * @param in 要转化的字符串
     * @return 转化后的字符串
     */
    private String PagetoDB(String in){
        try {
            if(in != null) {
                //System.out.println("inStr != null") ;
                return new String(in.getBytes("GBK"), "ISO8859_1") ;
            } else {
                return "" ;
            }
        } catch(Exception ex) {
            //System.out.println("PagetoDB error:") ;
            return "";
        }
    }

    /**
     * 处理WebOffice 插件的调用:保存文档的调用
     * @param docInfo 由基类DocServer传过来的WebOffice的客户端数据
     * @param msgObj WebOffice 插件的服务器端JavaBean
     * @return 如果执行成功返回true
     */
    protected boolean saveFile(DocInfo docInfo, iMsgServer2000 msgObj) {
        //System.out.println(" here !") ;
        boolean ret = false;
        try {
            //得到保存的类型
            String saveType = msgObj.GetMsgByName("SAVETYPE");
            //System.out.println("SAVETYPE:"+saveType) ;

            boolean docversionopen = false;
            
            RecordSet rs = new RecordSet() ;
            int versionId = (docInfo.getRecordID().indexOf("_")>-1)?Util.getIntValue(docInfo.getRecordID().substring(0,docInfo.getRecordID().indexOf("_")),0):Util.getIntValue(docInfo.getRecordID(),0);
            int docId = (docInfo.getRecordID().indexOf("_")>-1)?Util.getIntValue(docInfo.getRecordID().substring(docInfo.getRecordID().indexOf("_")+1),0):0;
            int imagefileid = 0;
            rs.executeSql("select docid,imagefileid from docimagefile where versionId = " + versionId + (docId>0?" and docid = " + docId:""));
            if(rs.next()){
            	DocComInfo doccominfo = new DocComInfo();
            	int docscc = doccominfo.getDocSecCategory(Util.null2String(rs.getString(1)));
            	imagefileid = Util.getIntValue(Util.null2String(rs.getString(2)));
            	SecCategoryComInfo scc = new SecCategoryComInfo();
            	docversionopen = scc.isEditionOpen(docscc);
            }
            
            boolean otherrelated = false;
            rs.executeSql("select imagefileused from imagefile where imagefileid = " + imagefileid);
            if(rs.next()&&Util.getIntValue(Util.null2String(rs.getString(1)))>1) otherrelated = true;
            
            if(saveType.equalsIgnoreCase("EDIT")){    //处理编辑的操作
            	
            	if(docversionopen){
            		return saveFileForNewVersion(docInfo,msgObj);
            	}else if(otherrelated){
            		return saveFileForVersion(docInfo,msgObj);           		
            	}else{
            		return saveFileForEdit(docInfo,msgObj);
            	}
            }else if(saveType.equalsIgnoreCase("NEWVERSION")){   //处理保存新版本的操作
            	
            	if(docversionopen){
           			return saveFileForNewVersion(docInfo,msgObj);
            	} else{
            		return saveFileForVersion(docInfo,msgObj);
            	}
            }else{ //对普通的增加文档的操作
            	
            	return saveFileForNew(docInfo,msgObj);
            	
            }
        } catch(Exception ex) {
            //ex.printStackTrace(System.out) ;
            ret = false;
        }

        return ret;
    }
    	
    private boolean saveFileForNewVersion(DocInfo docInfo,iMsgServer2000 msgObj) throws Exception {
    	
    	//文件大小判断开始
    	int mFileSize=msgObj.MsgFileSize();
    	if(mFileSize<=5*1024){//小于5K，认为文档出错。
    		return false;
    	}
    	
    	int newFS=Util.getIntValue(msgObj.GetMsgByName("NEWFS"),-1);
//    	if(newFS>0&&newFS!=mFileSize){
//    		return false;   		
//    	}
    	//文件大小判断结束 
    	
        RecordSet rs = new RecordSet() ;

        //得到新版本的描述
        String versionDetail = msgObj.GetMsgByName("VERSIONDETAIL") ;
        //System.out.println("versionDetail:"+versionDetail) ;

        //得到当前的版本号
        //int versionId = Util.getIntValue(docInfo.getRecordID(),0);
        int versionId = (docInfo.getRecordID().indexOf("_")>-1)?Util.getIntValue(docInfo.getRecordID().substring(0,docInfo.getRecordID().indexOf("_")),0):Util.getIntValue(docInfo.getRecordID(),0);
        int docId = (docInfo.getRecordID().indexOf("_")>-1)?Util.getIntValue(docInfo.getRecordID().substring(docInfo.getRecordID().indexOf("_")+1),0):0;
        
        //System.out.println("versionId:"+versionId) ;
        
        DocImageManager imgManger = new DocImageManager() ;
        imgManger.resetParameter() ;
        imgManger.setVersionId(versionId) ;
        imgManger.setDocid(docId);
        //根据传入的版本号得到同文档的最新版本的文档的信息
        imgManger.selectCurNewestVersion() ;
        imgManger.next() ;
        
        String oldImgId = imgManger.getImagefileid() ;
        String sql = "select * from ImageFile where imagefileid="+oldImgId;
        rs.executeSql(sql) ;
        rs.next() ;
        String imageFileName = rs.getString("imagefilename") ;
        String imageFileType = rs.getString("imagefiletype") ;
                
        SystemComInfo syscominfo = new SystemComInfo() ;
        String createdir = FileUpload.getCreateDir(syscominfo.getFilesystem()) ;
        
        String fileName = docInfo.createFileName();
        if(imageFileName==null||"".equals(imageFileName)) imageFileName = fileName + docInfo.getFileType();

        String needzip = syscominfo.getNeedzip();
        if("1".equals(needzip)){
        	fileName += ".zip";
        } else {
        	fileName += docInfo.getFileType();
        }

        String fullName = createdir + fileName ;
        
        int imageid = 0 ;
        
        String filerealpath = fullName;
        
        int fsize = 0;
        
       	FileManage.createDir(createdir);

    	boolean isError=false;       	
       	
        //写压缩文件
        OutputStream fileOut = null ;
        try{
        	File file = new File(filerealpath) ;
            if(needzip.equals("1")) {
                ZipOutputStream filezipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file))) ;
                filezipOut.setMethod(ZipOutputStream.DEFLATED) ; //设置压缩方法
                filezipOut.putNextEntry(new ZipEntry("d:\\test.doc")) ;
                fileOut = filezipOut ;
            } else {
                fileOut = new BufferedOutputStream(new FileOutputStream(file)) ;
            }
            //System.out.print("test") ;
            byte[] fileBody = msgObj.MsgFileBody() ;
            String clearHandwritten = Util.null2String(msgObj.GetMsgByName("CLEARHANDWRITTEN")) ;

            if(clearHandwritten.equalsIgnoreCase("TRUE")){
            	fileBody=msgObj.ToDocument(fileBody);
            }

            //System.out.print(" length is " + fileBody.length) ;
            fileOut.write(fileBody, 0, fileBody.length) ;
            
            fsize = fileBody.length;
            
        }catch(Exception ex){
        	isError=true;
        	writeLog("DocDbServer.saveFileForNewVersion="+ex);             	
            //
        }finally{
            fileOut.close() ;
        }

        if(isError){
        	return false;
        }          

        int newDocImageFileId=0;
        try{
            int docImageFileId=0;
            rs.executeSql("select * from DocImageFile where versionId="+versionId + (docId>0?" and docid = " + docId:"") + " order by id desc ");
            if(rs.next()){
            	docImageFileId=rs.getInt("id");
            	docId=rs.getInt("docid");          	
            }
            
            int tempDocId=0;
            if(docId>0){
            	tempDocId=-docId;
            }else{
            	tempDocId=docId;
            }
            
            int tempImageFileId=0;
            String tempimagefilename=null;
            String tempimagefiledesc=null;
            int tempimagefilewidth=0;
            int tempimagefileheight=0;
            int tempimagefielsize=0;            
            String tempdocfiletype=null;
            String tempversionid=null;
            String tempVersionDetail=null;
            String tempisextfile=null;
            String tempHasUsedTemplet=null;
            
            RecordSet recorder = new RecordSet();
            
            rs.executeSql("select * from DocImageFile where id=" + docImageFileId+" order by versionId asc ");
            while(rs.next()){
            	
            	if(newDocImageFileId<=0){
            		newDocImageFileId=imgManger.getNextDocImageFileId();
            	}
            	tempImageFileId=rs.getInt("imageFileId");
	            rs.executeProc("imagefile_AddByDoc", "" + tempImageFileId);
	        	
	            tempimagefilename = rs.getString("imagefilename");
	            tempimagefiledesc=""+tempImageFileId;
	            tempimagefilewidth = rs.getInt("imagefilewidth");
	            tempimagefileheight = rs.getInt("imagefileheight");
	            tempimagefielsize = rs.getInt("imagefielsize");
	            tempdocfiletype = rs.getString("docfiletype");
	            tempversionid = rs.getString("versionid");
	            tempVersionDetail = rs.getString("versionDetail");
	            tempisextfile = Util.null2String(Util.getIntValue(rs.getString("isextfile"), 0)+"");
	            tempHasUsedTemplet = Util.null2String(Util.getIntValue(rs.getString("hasUsedTemplet"), 0)+"");
	            
	            String tempsql = "insert into docimagefile(id,docid,imagefileid,imagefilename,imagefiledesc,"
	                    + "imagefilewidth,imagefileheight,imagefielsize,docfiletype,versionid,versionDetail,isextfile,hasUsedTemplet) values("
	                    + newDocImageFileId + "," + tempDocId + "," + tempImageFileId + ",'" + Util.toHtml100(tempimagefilename) + "','" + Util.toHtml100(tempimagefiledesc)
	                    + "'," + tempimagefilewidth + "," + tempimagefileheight + "," + tempimagefielsize + ",'" + tempdocfiletype + "','"
	                    + tempversionid + "','" + Util.toHtml100(tempVersionDetail) + "', '"+tempisextfile+"', '"+tempHasUsedTemplet+"')";
	            recorder.executeSql(tempsql);           	
            }
    
        }catch(Exception ex){
        	isError=true;        	
        	writeLog("ex="+ex);
        }        
 
        if(isError){
        	return false;
        }       
        
        // msgObj.MakeDirectory(createdir) ;
        // msgObj.MsgFileSave(fullName) ;
        // int fsize = msgObj.MsgFileSize() ;
        
//        rs = new RecordSet();
        char separator = Util.getSeparator() ;
        //rs.executeProc("SequenceIndex_SelectFileid", "") ;
        //if(rs.next()) {
        //    imageid = Util.getIntValue(rs.getString(1)) ;
        //}
        imageid=imageFileIdUpdate.getImageFileNewId();
        
        String para = "" + imageid + separator + imageFileName
                      + separator
                      + imageFileType + separator + "1" + separator
                      + fullName + separator
                      + Util.getIntValue(needzip,0) + separator + "0" + separator + fsize ;
        //调用存储过程，插入此上传文件的相关内容到相关的表
        rs.executeProc("ImageFile_Insert", para) ;
        
        if(docId>0) imgManger.setDocid(-docId);
        else imgManger.setDocid(-imgManger.getDocid()) ;
        //如果设置了id 则不不自动产生新的id好 说明id相同 时统一文档的不同版本
        //imgManger.setId(imgManger.getId()) ;
        imgManger.setId(newDocImageFileId);
        imgManger.setImagefiledesc(oldImgId);
        //System.out.println("test1:"+versionDetail) ;
        //System.out.println("test2:"+this.PagetoDB("test input end")) ;
        imgManger.setVersionDetail(versionDetail) ;
        //imgManger.setVersionDetail(versionDetail) ;
        //System.out.println("after to versionDetail:"+versionDetail) ;
        imgManger.setImagefileid(Util.getIntValue(imageid + "", 0)) ;
        
        //if(imgManger.getImagefilename()==null||"".equals(imgManger.getImagefilename())) imgManger.setImagefilename(imageFileName);
        //else
        //imgManger.setImagefilename(imgManger.getImagefilename());
        //imgManger.setDocfiletype(imgManger.getDocfiletype()) ;
        String docType = "" ;
        if(docInfo.getFileType().equals(".doc")) {
            docType = "3" ;
        }else if(docInfo.getFileType().equals(".xls")) {
            docType = "4" ;
        }else if(docInfo.getFileType().equals(".ppt")) {
            docType = "5" ;
        }else if(docInfo.getFileType().equals(".wps")) {
            docType = "6" ;
        }else if(docInfo.getFileType().equals(".docx")) {
            docType = "7" ;
        }else if(docInfo.getFileType().equals(".xlsx")) {
            docType = "8" ;
        }else if(docInfo.getFileType().equals(".pptx")) {
            docType = "9" ;
        }else if(docInfo.getFileType().equals(".et")) {
            docType = "10" ;
        }

        //更新此文档的相关信息到数据库
    	if(docInfo.getFileName().endsWith(docInfo.getFileType())){
    		imageFileName = docInfo.getFileName();
    	}else{
    		imageFileName = docInfo.getFileName() + docInfo.getFileType();
    	}
    	
    	imgManger.setImagefilename(imageFileName);        
    	imgManger.setDocfiletype(docType) ;         

        imgManger.setIsextfile(imgManger.getIsextfile());
        /*
        String docType = "" ;
        if(docInfo.getFileType().equals(".doc")) {

            docType = "3" ;
        } else if(docInfo.getFileType().equals(".xls")) {
            docType = "4" ;
        }
        imgManger.setDocfiletype(docType) ;
        */
        String hasUsedTemplet="0";
        if("1".equals(Util.null2String(msgObj.GetMsgByName("HASUSEDTEMPLET")))){
        	hasUsedTemplet="1";
        }
        imgManger.setHasUsedTemplet(hasUsedTemplet);
        imgManger.AddDocImageInfo() ;

        msgObj.SetMsgByName("VERSIONID", ""+imgManger.getVersionId2()) ; 
        
    	return true;
    }
    
    
    private boolean saveFileForNew(DocInfo docInfo,iMsgServer2000 msgObj) throws Exception {
    	
    	//文件大小判断开始
    	int mFileSize=msgObj.MsgFileSize();
    	if(mFileSize<=5*1024){//小于5K，认为文档出错。
    		return false;
    	}
    	
    	int newFS=Util.getIntValue(msgObj.GetMsgByName("NEWFS"),-1);
//    	if(newFS>0&&newFS!=mFileSize){
//    		return false;   		
//    	}
    	//文件大小判断结束     	
    	
        RecordSet rs = new RecordSet() ;

        int docId = new DocManager().getNextDocId(rs) ;
        
        DocImageManager imgManger = new DocImageManager() ;
        

        SystemComInfo syscominfo = new SystemComInfo() ;
        String createdir = FileUpload.getCreateDir(syscominfo.getFilesystem());
        String fileName = docInfo.createFileName();
        
        //String imageFileName = fileName + docInfo.getFileType();
        String imageFileName="";
        if(docInfo.getFileName()!=null&&!"".equals(docInfo.getFileName())){
        	//imageFileName = docInfo.getFileName() + docInfo.getFileType();
        	if(docInfo.getFileName().endsWith(docInfo.getFileType())){
        		imageFileName = docInfo.getFileName();
        	}else{
        		imageFileName = docInfo.getFileName() + docInfo.getFileType();
        	}
        }else{
        	imageFileName = fileName + docInfo.getFileType();
        }
        
        String needzip = syscominfo.getNeedzip();
        if("1".equals(needzip)){
        	fileName += ".zip";
        } else {
        	fileName += docInfo.getFileType();
        }
        
        String fullName = createdir + fileName ;
        
        String filerealpath = fullName;
        
        int fsize = 0;

    	FileManage.createDir(createdir);

    	boolean isError=false;      	
    	
        //写压缩文件
        OutputStream fileOut = null ;
        try{
        	File file = new File(filerealpath) ;
            if(needzip.equals("1")) {
                ZipOutputStream filezipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file))) ;
                filezipOut.setMethod(ZipOutputStream.DEFLATED) ; //设置压缩方法
                filezipOut.putNextEntry(new ZipEntry("d:\\test.doc")) ;
                fileOut = filezipOut ;
            } else {
                fileOut = new BufferedOutputStream(new FileOutputStream(file)) ;
            }
            //System.out.print("test") ;
            byte[] fileBody = msgObj.MsgFileBody() ;
            String clearHandwritten = Util.null2String(msgObj.GetMsgByName("CLEARHANDWRITTEN")) ;

            if(clearHandwritten.equalsIgnoreCase("TRUE")){
            	fileBody=msgObj.ToDocument(fileBody);
            }
            //System.out.print(" length is " + fileBody.length) ;
            fileOut.write(fileBody, 0, fileBody.length) ;
            
            fsize = fileBody.length;
            
        }catch(Exception ex){        	
        	isError=true;
        	writeLog("DocDbServer.saveFileForNew="+ex);       	
            //
        }finally{
            fileOut.close() ;
        }

        if(isError){
        	return false;
        }         
        
        // msgObj.MakeDirectory(createdir) ;
        // msgObj.MsgFileSave(fullName) ;
        // int fsize = msgObj.MsgFileSize() ;

        char separator = Util.getSeparator() ;
        int imageid = 0 ;
        //rs.executeProc("SequenceIndex_SelectFileid", "") ;
        //if(rs.next()) {
        //    imageid = Util.getIntValue(rs.getString(1)) ;
        //}
        imageid=imageFileIdUpdate.getImageFileNewId();
        
        String para = "" + imageid + separator + imageFileName
                      + separator
                      + "" + separator + "1" + separator
                      + fullName + separator
                      + Util.getIntValue(needzip,0) + separator + "0" + separator + fsize ;
        //调用存储过程，插入此上传文件的相关内容到相关的表
        rs.executeProc("ImageFile_Insert", para) ;
        
        
        imgManger.resetParameter() ;
        imgManger.setDocid(docId) ;
        imgManger.setImagefileid(Util.getIntValue(imageid + "", 0)) ;
        imgManger.setImagefilename(imageFileName) ;

        String docType = "" ;
        if(docInfo.getFileType().equals(".doc")) {

            docType = "3" ;
        }else if(docInfo.getFileType().equals(".xls")) {
            docType = "4" ;
        }else if(docInfo.getFileType().equals(".ppt")) {
            docType = "5" ;
        }else if(docInfo.getFileType().equals(".wps")) {
            docType = "6" ;
        }else if(docInfo.getFileType().equals(".docx")) {
            docType = "7" ;
        }else if(docInfo.getFileType().equals(".xlsx")) {
            docType = "8" ;
        }else if(docInfo.getFileType().equals(".pptx")) {
            docType = "9" ;
        }else if(docInfo.getFileType().equals(".et")) {
            docType = "10" ;
        }
        
        
        imgManger.setDocfiletype(docType) ;
        String hasUsedTemplet="0";
        if("1".equals(Util.null2String(msgObj.GetMsgByName("HASUSEDTEMPLET")))){
        	hasUsedTemplet="1";
        }
        imgManger.setHasUsedTemplet(hasUsedTemplet);  
        imgManger.AddDocImageInfo() ;

        //返回给客户端新创建的文档的文档id
        msgObj.SetMsgByName("CREATEID", docId + "") ;
        msgObj.SetMsgByName("DOCTYPE", docType) ;
        msgObj.SetMsgByName("VERSIONID", ""+imgManger.getVersionId2()) ;       

        return true;
    }
    
    private boolean saveFileForVersion(DocInfo docInfo,iMsgServer2000 msgObj) throws Exception {
    	
    	//文件大小判断开始
    	int mFileSize=msgObj.MsgFileSize();
    	if(mFileSize<=5*1024){//小于5K，认为文档出错。
    		return false;
    	}
    	
    	int newFS=Util.getIntValue(msgObj.GetMsgByName("NEWFS"),-1);
//    	if(newFS>0&&newFS!=mFileSize){
//    		return false;   		
//    	}
    	//文件大小判断结束     	
    	
        RecordSet rs = new RecordSet() ;

        //得到新版本的描述
        String versionDetail = msgObj.GetMsgByName("VERSIONDETAIL") ;
        //System.out.println("versionDetail:"+versionDetail) ;

        //得到当前的版本号
        //int versionId = Util.getIntValue(docInfo.getRecordID(),0);
        int versionId = (docInfo.getRecordID().indexOf("_")>-1)?Util.getIntValue(docInfo.getRecordID().substring(0,docInfo.getRecordID().indexOf("_")),0):Util.getIntValue(docInfo.getRecordID(),0);
        int docId = (docInfo.getRecordID().indexOf("_")>-1)?Util.getIntValue(docInfo.getRecordID().substring(docInfo.getRecordID().indexOf("_")+1),0):0;
        //System.out.println("versionId:"+versionId) ;
        DocImageManager imgManger = new DocImageManager() ;
        imgManger.resetParameter() ;
        imgManger.setVersionId(versionId) ;
        imgManger.setDocid(docId);
        //得到相应版本号的文档信息
        imgManger.selectDocImageInfoByVersion() ;
        imgManger.next() ;

        String oldImgId = imgManger.getImagefileid() ;
        String sql = "select * from ImageFile where imagefileid="+oldImgId;
        rs.executeSql(sql) ;
        rs.next() ;
        String imageFileName = rs.getString("imagefilename") ;
        String imageFileType = rs.getString("imagefiletype") ;
        
        SystemComInfo syscominfo = new SystemComInfo() ;
        String createdir = FileUpload.getCreateDir(syscominfo.getFilesystem()) ;
        
        String fileName = docInfo.createFileName();
        if(imageFileName==null||"".equals(imageFileName)) imageFileName = fileName + docInfo.getFileType();

        String needzip = syscominfo.getNeedzip();
        if("1".equals(needzip)){
        	fileName += ".zip";
        } else {
        	fileName += docInfo.getFileType();
        }

        String fullName = createdir + fileName ;
        
        int imageid = 0 ;
        
        String filerealpath = fullName;
        
        int fsize = 0;
        
    	FileManage.createDir(createdir);

    	boolean isError=false;    	
    	
        //写压缩文件
        OutputStream fileOut = null ;
        try{
        	File file = new File(filerealpath) ;
            if(needzip.equals("1")) {
                ZipOutputStream filezipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file))) ;
                filezipOut.setMethod(ZipOutputStream.DEFLATED) ; //设置压缩方法
                filezipOut.putNextEntry(new ZipEntry("d:\\test.doc")) ;
                fileOut = filezipOut ;
            } else {
                fileOut = new BufferedOutputStream(new FileOutputStream(file)) ;
            }
            //System.out.print("test") ;
            byte[] fileBody = msgObj.MsgFileBody() ;
            String clearHandwritten = Util.null2String(msgObj.GetMsgByName("CLEARHANDWRITTEN")) ;

            if(clearHandwritten.equalsIgnoreCase("TRUE")){
            	fileBody=msgObj.ToDocument(fileBody);
            }
            //System.out.print(" length is " + fileBody.length) ;
            fileOut.write(fileBody, 0, fileBody.length) ;
            
            fsize = fileBody.length;
            
        }catch(Exception ex){
        	isError=true;
        	writeLog("DocDbServer.saveFileForVersion="+ex);
            //
        }finally{
            fileOut.close() ;
        }

        if(isError){
        	return false;
        }          
        
        // msgObj.MakeDirectory(createdir) ;
        // msgObj.MsgFileSave(fullName) ;
        // int fsize = msgObj.MsgFileSize() ;
        
        rs = new RecordSet() ;
        char separator = Util.getSeparator() ;
        //rs.executeProc("SequenceIndex_SelectFileid", "") ;
        //if(rs.next()) {
        //    imageid = Util.getIntValue(rs.getString(1)) ;
        //}
        imageid=imageFileIdUpdate.getImageFileNewId();
        
        String para = "" + imageid + separator + imageFileName
                      + separator
                      + imageFileType + separator + "1" + separator
                      + fullName + separator
                      + Util.getIntValue(needzip,0) + separator + "0" + separator + fsize ;
        //调用存储过程，插入此上传文件的相关内容到相关的表
        rs.executeProc("ImageFile_Insert", para) ;
        
        imgManger.setDocid(imgManger.getDocid()) ;
        //如果设置了id 则不不自动产生新的id好 说明id相同 时统一文档的不同版本
        imgManger.setId(imgManger.getId()) ;
        //System.out.println("test1:"+versionDetail) ;
        //System.out.println("test2:"+this.PagetoDB("test input end")) ;
        imgManger.setVersionDetail(versionDetail) ;
        //imgManger.setVersionDetail(versionDetail) ;
        //System.out.println("after to versionDetail:"+versionDetail) ;
        imgManger.setImagefileid(Util.getIntValue(imageid + "", 0)) ;
        
        //if(imgManger.getImagefilename()==null||"".equals(imgManger.getImagefilename())) imgManger.setImagefilename(imageFileName);
        //else
        //imgManger.setImagefilename(imgManger.getImagefilename());
        //imgManger.setDocfiletype(imgManger.getDocfiletype()) ;
        String docType = "" ;
        if(docInfo.getFileType().equals(".doc")) {
            docType = "3" ;
        }else if(docInfo.getFileType().equals(".xls")) {
            docType = "4" ;
        }else if(docInfo.getFileType().equals(".ppt")) {
            docType = "5" ;
        }else if(docInfo.getFileType().equals(".wps")) {
            docType = "6" ;
        }else if(docInfo.getFileType().equals(".docx")) {
            docType = "7" ;
        }else if(docInfo.getFileType().equals(".xlsx")) {
            docType = "8" ;
        }else if(docInfo.getFileType().equals(".pptx")) {
            docType = "9" ;
        }else if(docInfo.getFileType().equals(".et")) {
            docType = "10" ;
        }

        //更新此文档的相关信息到数据库
    	if(docInfo.getFileName().endsWith(docInfo.getFileType())){
    		imageFileName = docInfo.getFileName();
    	}else{
    		imageFileName = docInfo.getFileName() + docInfo.getFileType();
    	}
    	
    	imgManger.setImagefilename(imageFileName);        
    	imgManger.setDocfiletype(docType) ;  	
    	
        
        imgManger.setIsextfile(imgManger.getIsextfile());
        /*
        String docType = "" ;
        if(docInfo.getFileType().equals(".doc")) {

            docType = "3" ;
        } else if(docInfo.getFileType().equals(".xls")) {
            docType = "4" ;
        }
        imgManger.setDocfiletype(docType) ;
        */
        String hasUsedTemplet="0";
        if("1".equals(Util.null2String(msgObj.GetMsgByName("HASUSEDTEMPLET")))){
        	hasUsedTemplet="1";
        }
        imgManger.setHasUsedTemplet(hasUsedTemplet);
        imgManger.AddDocImageInfo() ;

        msgObj.SetMsgByName("VERSIONID", ""+imgManger.getVersionId2()) ;         
    	
    	return true;
    }
    
    private boolean saveFileForEdit(DocInfo docInfo, iMsgServer2000 msgObj) throws Exception {
    	
    	//文件大小判断开始
    	int mFileSize=msgObj.MsgFileSize();
    	if(mFileSize<=5*1024){//小于5K，认为文档出错。
    		return false;
    	}
    	
    	int newFS=Util.getIntValue(msgObj.GetMsgByName("NEWFS"),-1);
//    	if(newFS>0&&newFS!=mFileSize){
//    		return false;   		
//    	}
    	//文件大小判断结束     	
    	
        RecordSet rs = new RecordSet() ;
        //int versionId = Util.getIntValue(docInfo.getRecordID(),0);
        int versionId = (docInfo.getRecordID().indexOf("_")>-1)?Util.getIntValue(docInfo.getRecordID().substring(0,docInfo.getRecordID().indexOf("_")),0):Util.getIntValue(docInfo.getRecordID(),0);
        int docId = (docInfo.getRecordID().indexOf("_")>-1)?Util.getIntValue(docInfo.getRecordID().substring(docInfo.getRecordID().indexOf("_")+1),0):0;

        //System.out.println("DocDbServer:versionId="+versionId) ;

        DocImageManager imgManger = new DocImageManager() ;
        imgManger.resetParameter() ;
        imgManger.setVersionId(versionId) ;
        imgManger.setDocid(docId);
        //根据传入的版本号得到同文档的最新版本的文档的信息
        imgManger.selectCurNewestVersion() ;
        imgManger.next() ;
        int newVid = imgManger.getVersionId() ;
        String imageFileId = imgManger.getImagefileid();

        //System.out.println("DocDbServer:versionId="+versionId+";newVid="+newVid+";imageFileId="+imageFileId) ;
        rs.executeSql("select * from ImageFile where imagefileid="+imageFileId);
        rs.next() ;
        
        String oldfilerealpath = Util.null2String(""+ rs.getString("filerealpath"));
        
        String createdir = "";
        String filename = "";
        String fileextname = "";
        
        SystemComInfo syscominfo = new SystemComInfo() ;
        
        String splitflag = "";
        if(oldfilerealpath.indexOf("\\")>-1) splitflag = "\\";
        if(oldfilerealpath.indexOf("/")>-1) splitflag = "/";
        
        String[] split = Util.TokenizerString2(oldfilerealpath, splitflag);
        
        for(int i=0;split!=null&&split.length>0&&i<split.length-1;i++){
        	createdir+=splitflag+split[i];
        }
        if(!createdir.endsWith(splitflag)) createdir+=splitflag;
//        if(createdir.startsWith(splitflag)) createdir = createdir.substring(1);
        boolean isStartsWitSplitflag=oldfilerealpath.startsWith(splitflag)?true:false;
        if(!isStartsWitSplitflag){//如果之前不以分隔符开始，则清除路径的首字符
        	if(createdir.startsWith(splitflag)){
        		createdir = createdir.substring(1);
        	}
        }
        
        int flag = split[split.length-1].indexOf(".");
        if(flag>-1){
        	filename = split[split.length-1].substring(0,flag);
        	fileextname = split[split.length-1].substring(flag);
        } else {
        	filename = split[split.length-1];
        	fileextname = "";
        }
        
        String needzip = syscominfo.getNeedzip();
        if("1".equals(needzip)){
        	filename += ".zip";
        } else {
        	filename += docInfo.getFileType();
        }
        
        String filerealpath = createdir + filename;
        int fsize = 0;
        
    	FileManage.createDir(createdir);

    	boolean isError=false;    	
    	
        //写压缩文件
        OutputStream fileOut = null ;
        try{
        	File file = new File(filerealpath) ;
            if(needzip.equals("1")) {
                ZipOutputStream filezipOut = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file))) ;
                filezipOut.setMethod(ZipOutputStream.DEFLATED) ; //设置压缩方法
                filezipOut.putNextEntry(new ZipEntry("d:\\test.doc")) ;
                fileOut = filezipOut ;
            } else {
                fileOut = new BufferedOutputStream(new FileOutputStream(file)) ;
            }
            //System.out.print("test") ;
            byte[] fileBody = msgObj.MsgFileBody() ;
            String clearHandwritten = Util.null2String(msgObj.GetMsgByName("CLEARHANDWRITTEN")) ;

            if(clearHandwritten.equalsIgnoreCase("TRUE")){
            	fileBody=msgObj.ToDocument(fileBody);
            }
            //System.out.print(" length is " + fileBody.length) ;
            fileOut.write(fileBody, 0, fileBody.length) ;
            
            fsize = fileBody.length;
        }catch(Exception ex){
            //ex.printStackTrace();
        	isError=true;
        	writeLog("DocDbServer.saveFileForEdit="+ex);        	
        }finally{
            fileOut.close() ;
        }

        if(isError){
        	return false;
        }        
        
        String docType = "" ;
        if(docInfo.getFileType().equals(".doc")) {
            docType = "3" ;
        }else if(docInfo.getFileType().equals(".xls")) {
            docType = "4" ;
        }else if(docInfo.getFileType().equals(".ppt")) {
            docType = "5" ;
        }else if(docInfo.getFileType().equals(".wps")) {
            docType = "6" ;
        }else if(docInfo.getFileType().equals(".docx")) {
            docType = "7" ;
        }else if(docInfo.getFileType().equals(".xlsx")) {
            docType = "8" ;
        }else if(docInfo.getFileType().equals(".pptx")) {
            docType = "9" ;
        }else if(docInfo.getFileType().equals(".et")) {
            docType = "10" ;
        }

        //更新此文档的相关信息道数据库
        //String imageFileName = docInfo.getFileName() ;
        //String imageFileName = docInfo.getFileName() +docInfo.getFileType();
        String imageFileName=null;
    	if(docInfo.getFileName().endsWith(docInfo.getFileType())){
    		imageFileName = docInfo.getFileName();
    	}else{
    		imageFileName = docInfo.getFileName() + docInfo.getFileType();
    	}
    	
        String hasUsedTemplet="0";
        if("1".equals(Util.null2String(msgObj.GetMsgByName("HASUSEDTEMPLET")))){
        	hasUsedTemplet="1";
        }    	
    	
        //String sql = "update DocImageFile set imagefilename='"+imageFileName+"' ,docfiletype="+docType+" where versionId="+newVid;
        String sql = "update DocImageFile set imagefilename='"+Util.toHtml100(imageFileName)+"' ,docfiletype="+docType+",hasUsedTemplet='"+hasUsedTemplet+"' where versionId="+newVid+ (docId>0?" and docid = " + docId:"");        
        rs.executeSql(sql) ;
        //sql = "update ImageFile set imagefilename='"+imageFileName+"',filerealpath='" + filerealpath + "',iszip=" + Util.getIntValue(needzip,0) + ",fileSize=" + fsize + " where imagefileid="+imageFileId;
        //sql = "update ImageFile set filerealpath='" + filerealpath + "',iszip=" + Util.getIntValue(needzip,0) + ",fileSize=" + fsize + " where imagefileid="+imageFileId;
        sql = "update ImageFile set imagefilename='"+Util.toHtml100(imageFileName)+"',filerealpath='" + filerealpath + "',iszip=" + Util.getIntValue(needzip,0) + ",fileSize=" + fsize + " where imagefileid="+imageFileId;
        rs.executeSql(sql) ;
    
        return true;
    }
    

    /**
     * 处理WebOffice 插件的调用：Load文档的调用
     * @param docInfo 由基类DocServer传过来的WebOffice的客户端数据
     * @param msgObj WebOffice 插件的服务器端JavaBean
     * @return 如果执行成功返回true
     */
    protected boolean loadFile(DocInfo docInfo,iMsgServer2000 msgObj){
        boolean ret = false;
        BufferedInputStream imagefile = null ;
        ConnStatement statement = null;
        ByteArrayOutputStream out = null ;         
        try {
            //String vId = docInfo.getRecordID() ;
        	String vId = ""+((docInfo.getRecordID().indexOf("_")>-1)?Util.getIntValue(docInfo.getRecordID().substring(0,docInfo.getRecordID().indexOf("_")),0):Util.getIntValue(docInfo.getRecordID(),0));
        	int docId = (docInfo.getRecordID().indexOf("_")>-1)?Util.getIntValue(docInfo.getRecordID().substring(docInfo.getRecordID().indexOf("_")+1),0):0;
        	
            if(vId.trim().equals("")){
                return true;
            }
            RecordSet rs = new RecordSet() ;
            statement = new ConnStatement();
            String sql = "select b.filerealpath as filerealpath,b.iszip as iszip,b.imagefile as imagefile from DocImageFile a ,ImageFile b where a.imagefileid=b.imagefileid and a.versionId="+ vId+(docId>0?" and a.docid = " + docId:"");
            boolean isoracle = (statement.getDBType()).equals("oracle") ;

            statement.setStatementSql(sql);
            statement.executeQuery();
            if(statement.next()){
                String filerealpath = Util.null2String(""+ statement.getString("filerealpath")) ;
                String iszip = Util.null2String(""+ statement.getString("iszip")) ;
                if(filerealpath.trim().equals("")){
                    //System.out.println("filerealpath is null");
                    if( isoracle ) imagefile = new BufferedInputStream(statement.getBlobBinary("imagefile")) ;
                    else imagefile = new BufferedInputStream(statement.getBinaryStream("imagefile")) ;

                }else{
                    //System.out.println("filerealpath is not null");
                    File thefile = new File(filerealpath) ;
                    if(iszip.equals("1")) {
                        ZipInputStream zin = new ZipInputStream(new FileInputStream(thefile)) ;
                        if(zin.getNextEntry() != null)
                        imagefile = new BufferedInputStream(zin) ;
                    } else{
                        imagefile = new BufferedInputStream(new FileInputStream(thefile)) ;
                    }
                }
                //System.out.println("had get imgefile stream");
                int byteread ;
                byte data[]=new byte[1024];
                //ByteArrayOutputStream out = new ByteArrayOutputStream() ;
                out = new ByteArrayOutputStream() ;                  
                while((byteread = imagefile.read(data)) != -1) {
                    out.write(data, 0, byteread) ;
                    out.flush() ;
                }
                byte[] fileBody = out.toByteArray();
                msgObj.MsgFileBody(fileBody);			//将文件信息打包
                msgObj.MsgFileSize(fileBody.length);

                //System.out.println("sucessful");
                ret = true;
            }
        } catch(Exception ex) {
            ex.printStackTrace() ;
            ret = false;
        }finally {
            try{
                statement.close() ;
            }catch(Exception ex){
            }
            try{
            	if(imagefile!=null){
            		imagefile.close() ;
            	}
            }catch(Exception ex){

            }
            try{
            	if(out!=null){ 
            		out.close() ;
            	}
            }catch(Exception ex){
            } 
        }

        return ret;
    }

    /**
     * 处理WebOffice 插件的调用：Load文档编辑模板的调用
     * @param docInfo 由基类DocServer传过来的WebOffice的客户端数据
     * @param msgObj WebOffice 插件的服务器端JavaBean
     * @return 如果执行成功返回true
     */
    protected boolean loadEditTemplate(DocInfo docInfo,iMsgServer2000 msgObj){
        boolean ret = false;
        try {
            int id = Util.getIntValue(docInfo.getTemplate(),0) ;
            //System.out.println("id:"+id) ;
            weaver.docs.mouldfile.MouldManager mManager = new weaver.docs.mouldfile.MouldManager();//编辑模板
            mManager.setId(id) ;
            mManager.getMouldInfoById() ;
            String filePath="";
            filePath = mManager.getMouldPath() ;
            mManager.closeStatement() ;
            //System.out.println("filePath:"+filePath) ;
            msgObj.MsgFileLoad(filePath) ;
            ret = true;
        } catch(Exception ex) {
            ex.printStackTrace() ;
            ret = false;
        }

        return ret;
    }
    
    /**
     * 处理WebOffice 插件的调用：Load文档显示模板的调用
     * @param docInfo 由基类DocServer传过来的WebOffice的客户端数据
     * @param msgObj WebOffice 插件的服务器端JavaBean
     * @return 如果执行成功返回true
     */
	protected boolean loadViewTemplate(DocInfo docInfo, iMsgServer2000 msgObj) {
		boolean ret = false;
		try {
			int id = Util.getIntValue(docInfo.getTemplate(), 0);
			weaver.docs.mould.MouldManager mManager = new weaver.docs.mould.MouldManager();//显示模板
			mManager.setId(id);
			mManager.getMouldInfoById();
			String filerealpath = mManager.getMouldPath();
			if (msgObj.MsgFileLoad(filerealpath))
			ret = true;
		} catch (Exception ex) {
			ex.printStackTrace();
			ret = false;
		}

		return ret;
	}
    
    /**
     * 处理WebOffice 插件的调用：显示模板插入正文的调用
     * @param docInfo 由基类DocServer传过来的WebOffice的客户端数据
     * @param msgObj WebOffice 插件的服务器端JavaBean
     * @return 如果执行成功返回true
     */
	protected boolean insertFile(DocInfo docInfo, iMsgServer2000 msgObj) {
        boolean ret = false;
        BufferedInputStream imagefile = null ;
        ConnStatement statement = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream() ;        
        try {
            //String vId = docInfo.getRecordID() ;
        	String vId = ""+((docInfo.getRecordID().indexOf("_")>-1)?Util.getIntValue(docInfo.getRecordID().substring(0,docInfo.getRecordID().indexOf("_")),0):Util.getIntValue(docInfo.getRecordID(),0));
            int docId = (docInfo.getRecordID().indexOf("_")>-1)?Util.getIntValue(docInfo.getRecordID().substring(docInfo.getRecordID().indexOf("_")+1),0):0;
        	
            if(vId.trim().equals("")){
                return true;
            }
            statement = new ConnStatement();
            String sql = "select b.filerealpath as filerealpath,b.iszip as iszip,b.imagefile as imagefile from DocImageFile a ,ImageFile b where a.imagefileid=b.imagefileid and a.versionId="+ vId+(docId>0?" and a.docid = " + docId:"");;
            boolean isoracle = (statement.getDBType()).equals("oracle") ;

            statement.setStatementSql(sql);
            statement.executeQuery();
            if(statement.next()){
                String filerealpath = Util.null2String(""+ statement.getString("filerealpath")) ;
                String iszip = Util.null2String(""+ statement.getString("iszip")) ;
                if(filerealpath.trim().equals("")){
                    //System.out.println("filerealpath is null");
                    if( isoracle ) imagefile = new BufferedInputStream(statement.getBlobBinary("imagefile")) ;
                    else imagefile = new BufferedInputStream(statement.getBinaryStream("imagefile")) ;

                }else{
                    //System.out.println("filerealpath is not null");
                    File thefile = new File(filerealpath) ;
                    if(iszip.equals("1")) {
                        ZipInputStream zin = new ZipInputStream(new FileInputStream(thefile)) ;
                        if(zin.getNextEntry() != null)
                        imagefile = new BufferedInputStream(zin) ;
                    } else{
                        imagefile = new BufferedInputStream(new FileInputStream(thefile)) ;
                    }
                }
                //System.out.println("had get imgefile stream");
                int byteread ;
                byte data[]=new byte[1024];
                //ByteArrayOutputStream out = new ByteArrayOutputStream() ;
                out = new ByteArrayOutputStream() ;
                while((byteread = imagefile.read(data)) != -1) {
                    out.write(data, 0, byteread) ;
                    out.flush() ;
                }
                byte[] fileBody = out.toByteArray();
                
                msgObj.MsgFileBody(fileBody);//将文件信息打包
                msgObj.MsgFileSize(fileBody.length);

                //System.out.println("sucessful");
                ret = true;
            }
        } catch(Exception ex) {
            ex.printStackTrace() ;
            ret = false;
        }finally {
            try{
                statement.close() ;
            }catch(Exception ex){
            }
            try{
            	if(imagefile!=null){
            		imagefile.close() ;
            	}
            }catch(Exception ex){

            }
            try{
            	if(out!=null){ 
            		out.close() ;
            	}
            }catch(Exception ex){
            } 
        }

		return ret;
	}
	
    /**
     * 处理WebOffice 插件的调用：读取书签信息放到DocInfo里
     * @param docInfo 由基类DocServer传过来的WebOffice的客户端数据
     * @param msgObj WebOffice 插件的服务器端JavaBean
     * @return 如果执行成功返回true
     */
    protected boolean loadBookMarks(DocInfo docInfo,iMsgServer2000 msgObj){
        boolean ret = false;
        try {
            RecordSet rs = new RecordSet() ;
            MouldBookMarkComInfo mbmci = new MouldBookMarkComInfo();

            int mouldid = Util.getIntValue(docInfo.getTemplate(), 0);
			int docid = Util.getIntValue(docInfo.getRecordID(),0);
			
			String showDocMouldBookMark=Util.null2String(msgObj.GetMsgByName("SHOWDOCMOULDBOOKMARK"));//是否显示“文档模板书签表”数据
			
		if(showDocMouldBookMark.equals("1")){	
			
			//直接取文档书签对应值填充模版
			//rs.executeSql("select * from docmouldbookmark where docid = "+docid);
			String sql=null;
			if(mouldid>0){
				sql="select * from docmouldbookmark where docid = "+docid+" and mouldId="+mouldid;
			}else{
				sql="select * from docmouldbookmark where docid = "+docid;
			}
			rs.executeSql(sql);			
			while(rs.next()){
				int bookmarkid = rs.getInt("bookmarkid");
				String bookmarkvalue = Util.null2String(rs.getString("bookmarkvalue"));
				String bookmarkname = mbmci.getMouldBookMarkName(bookmarkid+"");
				msgObj.SetMsgByName(bookmarkname,bookmarkvalue);
			}
		}else{
			DocManager dm = new DocManager();
			dm.resetParameter();
			dm.setId(docid);
			dm.getDocInfoById();
			int secid = dm.getSeccategory();
			
			SecCategoryDocPropertiesComInfo scdpci = new SecCategoryDocPropertiesComInfo();
			Map bookmarkmap = scdpci.getDocPropertiesValue(secid+"", docid+"");
			
			//取子目录模版设置中字段关联文档属性页的值填充
			rs.executeSql(" select " +
					" a.bookmarkid as bookmarkid,c.type as type,c.iscustom as iscustom,c.fieldid as fieldid " +
					" from DocSecCategoryMouldBookMark a ,DocSecCategoryMould b ,DocSecCategoryDocProperty c " +
					" where " +
					" a.DocSecCategoryMouldId = b.id " +
					" and a.DocSecCategoryDocPropertyId = c.id " +
					" and b.SecCategoryid = c.SecCategoryid " +
					" and b.MouldId = " + mouldid + 
					" and c.secCategoryId = " + secid
					);
			while(rs.next()){
				int bookmarkid = rs.getInt("bookmarkid");
				int isCustom = rs.getInt("iscustom");
				String bookmarkvalue = "";
				if(isCustom==1){//自定义字段
					int fieldid = rs.getInt("fieldid");
					CustomFieldManager cfm = new CustomFieldManager("DocCustomFieldBySecCategory",secid);
				    cfm.getCustomFields(fieldid);
					cfm.getCustomData(docid);
				    //if(cfm.next())
				    //	bookmarkvalue = cfm.getData("field"+cfm.getId());
				    if(cfm.next()){
				    	bookmarkvalue = cfm.getData("field"+cfm.getId());
				    	bookmarkvalue = cfm.getFieldValue(bookmarkvalue,getUser());
				    }
				} else {//文档属性
					int type = rs.getInt("type");
					switch(type){
						case 1:{//1 文档标题
							bookmarkvalue = (String) bookmarkmap.get("docsubject");
							
							break;
						}
						case 2:{//2 文档编号
							bookmarkvalue = (String) bookmarkmap.get("doccode");
							
							break;
						}
						case 3:{//3 发布
							if(bookmarkmap.get("docpublishtype").equals("1")) bookmarkvalue=SystemEnv.getHtmlLabelName(1984,getUser().getLanguage());
							if(bookmarkmap.get("docpublishtype").equals("2")) bookmarkvalue=SystemEnv.getHtmlLabelName(227,getUser().getLanguage());
							if(bookmarkmap.get("docpublishtype").equals("3")) bookmarkvalue=SystemEnv.getHtmlLabelName(229,getUser().getLanguage());
							
							break;
						}
						case 4:{//4 文档版本
							//bookmarkvalue = (String) bookmarkmap.get("docedition");
					    	int  docEdition=0;
					    	int  editionIsOpen=0;
					    	String editionPrefix=null;
					    	RecordSet rsTemp=new RecordSet();
					        rsTemp.executeSql(
					                " select a.docedition,b.editionIsOpen,b.editionPrefix " +
					                " from docdetail a, docseccategory b " +
					                " where a.seccategory = b.id " +		                
					                " and a.id = " + docid
					        );			        
					        if(rsTemp.next()){
					        	docEdition=Util.getIntValue(rsTemp.getString("docEdition"),-1);
					        	editionIsOpen=Util.getIntValue(rsTemp.getString("editionIsOpen"),-1);
					        	editionPrefix=Util.null2String(rsTemp.getString("editionPrefix"));
					        }
					        
					        bookmarkvalue="";
					        if(editionIsOpen==1){
					        	if(docEdition==-1){
					        		docEdition=0;
					        	}
					        	bookmarkvalue=editionPrefix+(docEdition)+ ".0";
					        }					    	
							break;
						}
						case 5:{//5 文档状态
//							if(bookmarkmap.get("docstatus").equals("0")||Util.getIntValue((String)bookmarkmap.get("docstatus"),0)<=0) bookmarkvalue=SystemEnv.getHtmlLabelName(1984,getUser().getLanguage());
//							if(bookmarkmap.get("docstatus").equals("1")) bookmarkvalue=SystemEnv.getHtmlLabelName(227,getUser().getLanguage());
//							if(bookmarkmap.get("docstatus").equals("2")) bookmarkvalue=SystemEnv.getHtmlLabelName(229,getUser().getLanguage());
//							if(bookmarkmap.get("docstatus").equals("3")) bookmarkvalue=SystemEnv.getHtmlLabelName(1984,getUser().getLanguage());
//							if(bookmarkmap.get("docstatus").equals("5")) bookmarkvalue=SystemEnv.getHtmlLabelName(227,getUser().getLanguage());
//							if(bookmarkmap.get("docstatus").equals("6")) bookmarkvalue=SystemEnv.getHtmlLabelName(229,getUser().getLanguage());
//							if(bookmarkmap.get("docstatus").equals("7")) bookmarkvalue=SystemEnv.getHtmlLabelName(1984,getUser().getLanguage());
//							if(bookmarkmap.get("docstatus").equals("8")) bookmarkvalue=SystemEnv.getHtmlLabelName(227,getUser().getLanguage());
							
							String docStatusId=Util.null2String((String)bookmarkmap.get("docstatus"));
							if(docStatusId.equals("0")||Util.getIntValue(docStatusId,0)<=0) bookmarkvalue=SystemEnv.getHtmlLabelName(220,getUser().getLanguage());
					         if (docStatusId.equals("1") || docStatusId.equals("2")) {
					        	 DocComInfo dc=new DocComInfo();
					        	 int docSecCategory=dc.getDocSecCategory(docid+"");
					        	 SecCategoryComInfo secCategoryComInfo=new SecCategoryComInfo();
					             if(secCategoryComInfo.isControledByDir(docSecCategory))
					            	 bookmarkvalue = SystemEnv.getHtmlLabelName(18431,getUser().getLanguage());
					             else
					            	 bookmarkvalue = SystemEnv.getHtmlLabelName(225,getUser().getLanguage());
					         }
							if(docStatusId.equals("3")) bookmarkvalue=SystemEnv.getHtmlLabelName(359,getUser().getLanguage());
							if(docStatusId.equals("4")) bookmarkvalue=SystemEnv.getHtmlLabelName(236,getUser().getLanguage());							
							if(docStatusId.equals("5")) bookmarkvalue=SystemEnv.getHtmlLabelName(251,getUser().getLanguage());
							if(docStatusId.equals("6")) bookmarkvalue=SystemEnv.getHtmlLabelName(19564,getUser().getLanguage());
							if(docStatusId.equals("7")) bookmarkvalue=SystemEnv.getHtmlLabelName(15750,getUser().getLanguage());
							if(docStatusId.equals("8")) bookmarkvalue=SystemEnv.getHtmlLabelName(15358,getUser().getLanguage());							
							break;
						}
						case 6:{//6 主目录
							bookmarkvalue = (String) bookmarkmap.get("maincategory");
					    	MainCategoryComInfo mainCategoryComInfo=new MainCategoryComInfo();
					    	bookmarkvalue=mainCategoryComInfo.getMainCategoryname(bookmarkvalue);							
							break;
						}
						case 7:{//7 分目录
							bookmarkvalue = (String) bookmarkmap.get("subcategory");
					    	SubCategoryComInfo subCategoryComInfo=new SubCategoryComInfo();
					    	bookmarkvalue=subCategoryComInfo.getSubCategoryname(bookmarkvalue);							
							break;
						}
						case 8:{//8 子目录
							bookmarkvalue = (String) bookmarkmap.get("seccategory");
					    	SecCategoryComInfo secCategoryComInfo=new SecCategoryComInfo();
					    	bookmarkvalue=secCategoryComInfo.getSecCategoryname(bookmarkvalue);							
							break;
						}
						case 9:{//9 部门
							bookmarkvalue = (String) bookmarkmap.get("docdepartmentid");
							DepartmentComInfo departmentComInfo=new DepartmentComInfo();
							bookmarkvalue=departmentComInfo.getDepartmentmark(bookmarkvalue);
							break;
						}
						case 10:{//10 模版设置
					    	int docMouldId = 0;
					    	DocManager docManager=new DocManager();
					    	docManager.resetParameter();
					    	docManager.setId(docid);
					    	docManager.getDocInfoById();
					    	int selectedPubMouldId = docManager.getSelectedPubMouldId();

					    	int secCategory=docManager.getSeccategory();
					    	
					    	if(selectedPubMouldId<=0){
						    	if(scdpci.getDocProperties(""+secCategory,"10")&&scdpci.getVisible().equals("1")){
						    		SecCategoryMouldComInfo secCategoryMouldComInfo=new SecCategoryMouldComInfo();
						    		if(secCategoryMouldComInfo.getDefaultMould(""+secCategory,"3","2")){
						    			docMouldId = Util.getIntValue(secCategoryMouldComInfo.getMouldId(),0);
						    		} else if(secCategoryMouldComInfo.getDefaultMould(""+secCategory,"3","1")) {
						    			docMouldId = Util.getIntValue(secCategoryMouldComInfo.getMouldId(),0);
						    		}
						    	}
					    	}else{
						    	docMouldId=selectedPubMouldId;
					    	}
					    	
					    	if(docMouldId ==0){
					    		MouldManager mouldManager=new MouldManager();
					    		docMouldId = mouldManager.getDefaultWordMouldId();
					    	}
					    	DocMouldComInfo docMouldComInfo=new DocMouldComInfo();
					    	bookmarkvalue=docMouldComInfo.getDocMouldname(docMouldId+"");							
							
							break;
						}
						case 11:{//11 语言
							bookmarkvalue = (String) bookmarkmap.get("doclangurage");
					    	LanguageComInfo languageComInfo=new LanguageComInfo();
					    	bookmarkvalue=languageComInfo.getLanguagename(""+bookmarkvalue);							
							break;
						}
						case 12:{//12 关键字
							bookmarkvalue = (String) bookmarkmap.get("keyword");
							
							break;
						}
						case 13:{//13 创建
							//bookmarkvalue = (String) bookmarkmap.get("doccreaterid");
							int userId=Util.getIntValue((String) bookmarkmap.get("doccreaterid"),0);
							String userType=Util.null2String((String) bookmarkmap.get("docCreaterType"));		
							String date=Util.null2String((String) bookmarkmap.get("doccreatedate"));
							String time=Util.null2String((String) bookmarkmap.get("doccreatetime"));
							bookmarkvalue=getReturnValueByOperatorInfo(userId,userType,date,time);							
							break;
						}
						case 14:{//14 修改
							//bookmarkvalue = (String) bookmarkmap.get("doclastmoduserid");
							int userId=Util.getIntValue((String) bookmarkmap.get("doclastmoduserid"),0);
							String userType=Util.null2String((String) bookmarkmap.get("docLastModUserType"));		
							String date=Util.null2String((String) bookmarkmap.get("doclastmoddate"));
							String time=Util.null2String((String) bookmarkmap.get("doclastmodtime"));
							bookmarkvalue=getReturnValueByOperatorInfo(userId,userType,date,time);							
							break;
						}
						case 15:{//15 批准
							//bookmarkvalue = (String) bookmarkmap.get("docapproveuserid");
							int userId=Util.getIntValue((String) bookmarkmap.get("docapproveuserid"),0);
							String userType=Util.null2String((String) bookmarkmap.get("docApproveUserType"));		
							String date=Util.null2String((String) bookmarkmap.get("docapprovedate"));
							String time=Util.null2String((String) bookmarkmap.get("docapprovetime"));
							bookmarkvalue=getReturnValueByOperatorInfo(userId,userType,date,time);							
							break;
						}
						case 16:{//16 失效
							//bookmarkvalue = (String) bookmarkmap.get("docinvaluserid");
							int userId=Util.getIntValue((String) bookmarkmap.get("docinvaluserid"),0);
							String userType=Util.null2String((String) bookmarkmap.get("docInvalUserType"));		
							String date=Util.null2String((String) bookmarkmap.get("docinvaldate"));
							String time=Util.null2String((String) bookmarkmap.get("docinvaltime"));
							bookmarkvalue=getReturnValueByOperatorInfo(userId,userType,date,time);							
							break;
						}
						case 17:{//17 归档
							//bookmarkvalue = (String) bookmarkmap.get("docarchiveuserid");
							int userId=Util.getIntValue((String) bookmarkmap.get("docarchiveuserid"),0);
							String userType=Util.null2String((String) bookmarkmap.get("docArchiveUserType"));		
							String date=Util.null2String((String) bookmarkmap.get("docarchivedate"));
							String time=Util.null2String((String) bookmarkmap.get("docarchivetime"));
							bookmarkvalue=getReturnValueByOperatorInfo(userId,userType,date,time);							
							break;
						}
						case 18:{//18 作废
							//bookmarkvalue = (String) bookmarkmap.get("doccanceluserid");
							int userId=Util.getIntValue((String) bookmarkmap.get("doccanceluserid"),0);
							String userType=Util.null2String((String) bookmarkmap.get("docCancelUserType"));		
							String date=Util.null2String((String) bookmarkmap.get("doccanceldate"));
							String time=Util.null2String((String) bookmarkmap.get("doccanceltime"));
							bookmarkvalue=getReturnValueByOperatorInfo(userId,userType,date,time);							
							break;
						}
						case 19:{//19 主文档
							bookmarkvalue = (String) bookmarkmap.get("maindoc");
							DocComInfo dc=new DocComInfo();
							bookmarkvalue=dc.getDocname(bookmarkvalue);
							break;
						}
						case 20:{//20 被引用列表
							bookmarkvalue="";
							RecordSet rsTemp=new RecordSet();
					    	rsTemp.executeSql(" select  docSubject  from docdetail where mainDoc="+docid);
					    	while(rsTemp.next()){
					    		bookmarkvalue+=","+rsTemp.getString("docSubject");
					    	}
					    	if(!bookmarkvalue.equals("")){
					    		bookmarkvalue=bookmarkvalue.substring(1);
					    	}							
							break;
						}
						case 21:{//21 文档所有者
							int ownerid= Util.getIntValue((String) bookmarkmap.get("ownerid"),0);
							String ownerType= Util.null2String((String) bookmarkmap.get("ownerType"));	
							bookmarkvalue="";
							if(ownerid>0){
								if(ownerType.equals("2")){
									try{
										CustomerInfoComInfo customerInfoComInfo=new CustomerInfoComInfo();
										bookmarkvalue+=customerInfoComInfo.getCustomerInfoname(""+ownerid);				
									}catch(Exception ex){
										
									}				
								}else{
									try{
										ResourceComInfo resourceComInfo=new ResourceComInfo();
										bookmarkvalue+=resourceComInfo.getResourcename(""+ownerid);					
									}catch(Exception ex){
										
									}				
								}
							}							
							break;
						}
						case 22:{//22 失效日期
							bookmarkvalue = Util.null2String((String) bookmarkmap.get("invalidationDate"));						
							break;
						}
						case 24:{//24 虚拟目录
							bookmarkvalue="";
							DocTreeDocFieldComInfo docTreeDocFieldComInfo=new DocTreeDocFieldComInfo();
							String tempCatelogId=null;
							RecordSet rsTemp=new RecordSet();
					    	rsTemp.executeSql(" select catelogid from DocDummyDetail where docid="+docid);
					    	while(rsTemp.next()){
					    		tempCatelogId=Util.null2String(rsTemp.getString("catelogid"));
					    		bookmarkvalue+="，"+docTreeDocFieldComInfo.getTreeDocFieldName(tempCatelogId);
					    	}
					    	if(!bookmarkvalue.equals("")){
					    		bookmarkvalue=bookmarkvalue.substring(1);
					    	}					
							break;
						}						
					}
				}
				String bookmarkname = mbmci.getMouldBookMarkName(bookmarkid+"");
				bookmarkvalue=this.fromHtmlToWordDsp(bookmarkvalue);
				msgObj.SetMsgByName(bookmarkname,bookmarkvalue);
			}
			
		  }	

			ret = true;
    	
		} catch (Exception ex) {
			ex.printStackTrace();
			ret = false;
		}

		return ret;
    }

    /**
     * 处理WebOffice 插件的调用：Load签章列表的调用
     * @param docInfo 由基类DocServer传过来的WebOffice的客户端数据
     * @param msgObj WebOffice 插件的服务器端JavaBean
     * @return 如果执行成功返回true
     */
    protected boolean loadMarkList(DocInfo docInfo, iMsgServer2000 msgObj) {
        boolean ret = false;
        try {
            String userName = docInfo.getUserName() ;
            RecordSet rs = new RecordSet();
            String sql = "select markName from DocSignature where hrmresid="+userName.trim() ;
            //System.out.println("loadMarkList sql: "+sql) ;
            String markList = "";
            rs.executeSql(sql) ;
            while(rs.next() ){
                markList += rs.getString("MarkName")+"\r\n";
            }
            //System.out.println("loadMarkList markList: "+markList) ;
            docInfo.setMarkList(markList) ;
            ret = true;
        } catch(Exception ex) {
            ret = false;
        }

        return ret;
    }

    /**
     * 处理WebOffice 插件的调用：Load签章图片文件的调用
     * @param docInfo 由基类DocServer传过来的WebOffice的客户端数据
     * @param msgObj WebOffice 插件的服务器端JavaBean
     * @return 如果执行成功返回true
     */
    protected boolean loadMarkImage(DocInfo docInfo, iMsgServer2000 msgObj) {
        boolean ret = false;
        try {
            String markName = docInfo.getMarkName() ;
            RecordSet rs = new RecordSet();
            String sql = "select markType,markPath from DocSignature where hrmresid="+docInfo.getUserName()+" and markName='"+markName.trim()+"'";
            //System.out.println("loadMarkImage sql: "+sql);
            rs.executeSql(sql) ;
            if(rs.next() ){
                ret = true;
                docInfo.setFileType(rs.getString("MarkType")) ;
                docInfo.setFilePath(rs.getString("MarkPath")) ;
            }
            ret = true;
        } catch(Exception ex) {
            ret = false;
        }

        return ret;

    }

    /**
     * 处理WebOffice 插件的调用：保存签章相关信息的调用
     * @param docInfo 由基类DocServer传过来的WebOffice的客户端数据
     * @param msgObj WebOffice 插件的服务器端JavaBean
     * @return 如果执行成功返回true
     */
    protected boolean saveSignature(DocInfo docInfo, iMsgServer2000 msgObj) {
        boolean ret = false;
        ConnStatement stat = new ConnStatement();
        try {
            String versionId = docInfo.getRecordID() ;
            String markName = docInfo.getMarkName() ;
            String userName = docInfo.getUserName() ;
            String dateTime = docInfo.getDateTime() ;
            String hostName = docInfo.getHostName() ;
            String markGuid = docInfo.getMarkGuid() ;

            //String sql = "insert into DocDocumentSignature(versionId,markName,hrmresid,dateTime,hostName,markGuid) values(?,?,?,?,?,?)";
            //stat.setStatementSql(sql) ;
            //stat.setInt(1,Util.getIntValue(versionId,0)) ;
            //stat.setString(2,markName) ;
            //stat.setInt(3,Util.getIntValue(userName)) ;
            //stat.setString(4,dateTime) ;
            //stat.setString(5,hostName) ;
            //stat.setString(6,markGuid) ;
            //stat.executeUpdate() ;
            ret = true;
        } catch(Exception ex) {
            ret = false;
            this.writeLog(ex);
        } finally{
            try {
                stat.close() ;
            } catch(Exception ex) {
            }
        }

        return ret;

    }
	/**
	 * 根据操作者信息获得对应的值。
	 * 
	 * @param userId  操作者id
	 * @param userType 操作者类型
	 * @param date 日期
	 * @param time 时间
	 * 
	 * @return 值
	 */	
	private String getReturnValueByOperatorInfo(int userId,String userType,String date,String time){
		
		String returnValue="";

		
		//安全性检查
		if(userId<=0||userType==null||userType.trim().equals("")			
		){
			return "";
		}
		
		date=Util.null2String(date);
		time=Util.null2String(time);
		
		if(userId>0){
			if(userType.equals("2")){
				try{
					CustomerInfoComInfo customerInfoComInfo=new CustomerInfoComInfo();
					returnValue+=customerInfoComInfo.getCustomerInfoname(""+userId);				
				}catch(Exception ex){
					
				}				
			}else{
				try{
					ResourceComInfo resourceComInfo=new ResourceComInfo();
					returnValue+=resourceComInfo.getResourcename(""+userId);					
				}catch(Exception ex){
					
				}				
			}
			returnValue+=" "+date+" "+time;
		}
		return returnValue;
	}

	private static String fromHtmlToWordDsp(String htmlValue){
		//安全检查
		if(htmlValue==null){
			return "";
		}
		htmlValue=Util.StringReplace(htmlValue,"<br>",""+'\n');
		htmlValue=Util.StringReplace(htmlValue,"&lt;","<");	
		htmlValue=Util.StringReplace(htmlValue,"&gt;",">");	
		htmlValue=Util.StringReplace(htmlValue,"&quot;","\"");
		htmlValue=Util.StringReplace(htmlValue,"&nbsp;"," ");		
		
		return htmlValue;
	}
}