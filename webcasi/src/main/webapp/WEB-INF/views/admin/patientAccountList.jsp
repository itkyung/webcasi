<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>환자계정 관리</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/jquery/jqGrid-4.4.1/css/ui.jqgrid.css"/>
	<style>
		body { font-size: 62.5%; }
		label, input { display:block; }
		input.text { margin-bottom:12px; width:95%; padding: .4em; }
		fieldset { padding:0; border:0; margin-top:25px; }
		h1 { font-size: 1.2em; margin: .6em 0; }
		.ui-dialog .ui-state-error { padding: .3em; }
		.validateTips { border: 1px solid transparent; padding: 0.3em; }
	</style>      
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/script/jquery/jqGrid-4.4.1/js/jquery.jqGrid.min.js"></script>
    <script src="${pageContext.request.contextPath}/resources/script/jquery/jqGrid-4.4.1/js/i18n/grid.locale-kr.js" type="text/javascript"></script>
    <script>
    function openPage(id) {
    	$("#dialog-form").dialog("open");
    	$("#id").val(id);
    }
    
    $(function(){ 
	    $("#patientAccountGrid").jqGrid({
	    	url:_requestPath+"/admin/getPatientAccountList",
	    	datatype:"json",
	    	jsonReader:{
	    		root:"data",
	    		repeatitems:false
	    	},
	    	viewrecords:false,
	    	height:350,
	    	colNames:["아이디","성명","주민번호","로그인 아이디","패스워드","활성화"],
	    	colModel:[
					  {name:"id",index:"id",sortable:false,hidden:true},
	    	         {name:"name",index:"name",sortable:false},
	    	         {name:"resno",index:"resno",sortable:false},
	    	         {name:"loginId",index:"loginId",align:"right",sortable:false,hidden:true},
	    	         {name:"password",index:"password",sortable:false,hidden:true},
	    	         {name:"active",index:"active",align:"center",sortable:false}
	    	 ],
	        caption: "환자계정 관리"
	        ,loadComplete:function() {
	        	var grid = $('#patientAccountGrid');
	        	var ids = grid.jqGrid('getDataIDs');
	        	for(var i=0; i<ids.length; i++) {
	        		var id = ids[i];
	        		var rowData = grid.jqGrid('getRowData',id);
	        		grid.setRowData(id,{name:'<a href="javascript:openPage(\''+id+'\')"><font color=blue>'+rowData.name+'</font></a>'});
	        	}
	         }
	    });
    });
    
	$(function() {
		var id = $("#id"),
			name = $("#name"),
			loginId = $("#loginId"),
			password = $("#password"),
			active = $("#active"),
			allFields = $([]).add(id).add(name).add(loginId).add(password).add(active),
			tips = $(".validateTips");

		function updateTips(t) {
			tips.text(t).addClass("ui-state-highlight");
			setTimeout(function() {
				tips.removeClass("ui-state-highlight", 1500);
			}, 500 );
		}

		function checkLength(o, n, min, max) {
			if (o.val().length > max || o.val().length < min) {
				o.addClass("ui-state-error");
				updateTips(n + "의 길이는 꼭 " +
					min + " 과 " + max + "사이어야 합니다.");
				return false;
			} else {
				return true;
			}
		}

		function checkRegexp(o, regexp, n) {
			if (!(regexp.test(o.val()))) {
				o.addClass("ui-state-error");
				updateTips(n);
				return false;
			} else {
				return true;
			}
		}

		$("#dialog-form").dialog({
			autoOpen:false,
			height:230,
			width:400,
			modal:true,
			buttons:{
				"저장":function() {
					var bValid = true;
					allFields.removeClass("ui-state-error");
					
					if($("#password").val() != "") {
						//bValid = bValid && checkLength(name, "성명", 3, 50);
						//bValid = bValid && checkLength(loginId, "아이디", 6, 30);
						bValid = bValid && checkLength(password, "패스워드", 6, 30);

						//bValid = bValid && checkRegexp(name, /^[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]+$/i, "성명은 한글로만 구성되어야 합니다.");
						//bValid = bValid && checkRegexp(id, /^[a-z]([0-9a-z_])+$/i, "아이디는 a-z, 0-9, 언더라인으로 구성 되어지며 영문자로 시작하여야 합니다.");
						bValid = bValid && checkRegexp(password, /^[a-z]([0-9a-z])+$/i, "패스워드는 a-z, 0-9으로 구성돼며 영문자로 시작하여야 합니다.");
					}
					
					if(bValid) {
                        dataString = $("#patientAccountForm").serialize();
                        $.ajax({
                            type:"POST",
                            url:_requestPath+"/admin/savePatientPassword",
                            data:dataString,
                            dataType:"json",
                            success:function(data, textStatus, jqXHR) {
                            	if(!data.success) {
                            	    alert(data.message);       	
                            	} else {
                                	alert("변경되었습니다!");
                                	$("#dialog-form").dialog("close");
                            	    }
                            	},
                           error:function(jqXHR, textStatus, errorThrown) {
                            		alert(textStatus);
                            	}
                        });
					}
				},
				"닫기":function() {
					$(this).dialog("close");
				}
			},
			close: function() {
				allFields.val("").removeClass("ui-state-error");
			}
		});
	});
	
	
	function searchPatient() {
		var searchOpt = $("#searchOpt option:selected").val();
		var loginId = "";
		var name = "";
		var patientNo = "";
		if(searchOpt == 0) {
			loginId = $("#searchText").val();
		} else if(searchOpt == 1) {
			name = $("#searchText").val();
		} else if(searchOpt == 2) {
			patientNo = $("#searchText").val();
		}
		$("#patientAccountGrid").jqGrid('setGridParam',{url:_requestPath+"/admin/getPatientAccountList?loginId="+loginId+"&name="+name+"&patientNo="+patientNo}).trigger("reloadGrid");
	};
    </script>
</head>
<body>
	<div>
		<fieldset>
			<table>
				<tr>
					<td>
						<label for="name">검색조건:</label>
						<select id="searchOpt" name="searchOpt"><option value=0>주민번호</option><option value=1>성명</option><option value=2>수진자번호</option></select>
					</td>
					<td>
						<label for="loginId">검색어:</label>
						<input type="text" id="searchText" name="searchText"/>
					</td>
					<td>
						<label>조회:</label>
						<input type="button" value="검색" onclick="searchPatient();"/>		
					</td>				
				</tr>
			</table>
		</fieldset>
	</div> 

   <table id="patientAccountGrid"></table>
   
	<div id="dialog-form" title="환자 비밀번호 변경">
		<p class="validateTips"></p>
		<form id="patientAccountForm">
		<fieldset>
			<input type="hidden" name="id" id="id"/>	
			<label for="password">패스워드</label>
			<input type="text" name="password" id="password" class="text ui-widget-content ui-corner-all" />
		</fieldset>
		</form>
	</div>   
<!--    
	<div id="dialog-form" title="환자계정관리">
		<p class="validateTips"></p>
		<form id="patientAccountForm">
		<fieldset>
			<input type="hidden" name="id" id="id"/>	
			<label for="name">성명</label>
			<input type="text" name="name" id="name" class="text ui-widget-content ui-corner-all" readOnly=true/>
			<label for="loginId">로그인아이디</label>
			<input type="text" name="loginId" id="loginId" class="text ui-widget-content ui-corner-all" readOnly=true/>
			<label for="password">패스워드</label>
			<input type="text" name="password" id="password" class="text ui-widget-content ui-corner-all" />
			<label for="active">활성화</label>
			<input type="checkbox" name="active" id="active" value="1" class="ui-widget-content ui-corner-all" />
		</fieldset>
		</form>
	</div>
-->	
</body>
</html>