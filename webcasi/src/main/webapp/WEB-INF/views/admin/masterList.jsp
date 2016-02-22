<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>문진관리</title>
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
    	location.replace(_requestPath+"/admin/questionGroupList?masterId="+id);
    }
    $(function(){ 
	    $("#masterGrid").jqGrid({
	    	url:_requestPath+"/admin/getMasterList",
	    	datatype:"json",
	    	jsonReader:{
	    		root:"data",
	    		repeatitems:false
	    	},
	    	viewrecords:false,
	    	height:300,
	    	width:1020,
	    	colNames:["선택","제목","제목","버젼","활성화","상태"],
	    	colModel:[
	    	         {name:"id",index:"id",align:"center",sortable:false,formatter:function(cellValue, option) {
	    	        	    return "<input type='radio' name='masterId' value='" + option.rowId + "' />";
	    	          }},
	    	         {name:"title",index:"title",sortable:false},
	    	         {name:"titleText",index:"titleText",sortable:false,hidden:true},
	    	         {name:"version",index:"version",align:"right",sortable:false},
	    	         {name:"active",index:"active",align:"center",sortable:false,hidden:true},
	    	         {name:"status",index:"status",align:"center",sortable:false}
	    	 ],
	        caption: "문진버젼관"
	        ,loadComplete:function() {
	        	var grid = $('#masterGrid');
	        	var ids = grid.jqGrid('getDataIDs');
	        	for(var i=0; i<ids.length; i++) {
	        		var id = ids[i];
	        		var rowData = grid.jqGrid('getRowData',id);
	        		grid.setRowData(id,{title:'<a href="javascript:openPage(\''+id+'\')"><font color=blue>'+rowData.title+'</font></a>'});
	        		grid.setRowData(id,{titleText:rowData.title});
	        	}
	         }
	    });
    });
    
	$(function() {
		var id = $("#id"),
		   title = $("#title"),
			version = $("#version"),
			active = $("#active"),
			status = $("#status"),
			allFields = $([]).add(id).add(title).add(version).add(active),
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
		
		function checkStatus(originStatus, status) {
			if(originStatus == "SAVED") {
				if(status != "SAVED" && status != "COMPLETED") {
					updateTips("상태설정이 잘 못 되었습니다.");
					return false;
				} else {
					return true;
				}
			} else if(originStatus == "COMPLETED") {
				return true;
			} else if(originStatus == "ACTIVE") {
				if(status != "ACTIVE" && status != "INACTIVE") {
					updateTips("상태설정이 잘 못 되었습니다.");
					return false;
				} else {
					return true;
				}
			} else if(originStatus == "INACTIVE") {
				if(status != "INACTIVE" && status != "ACTIVE") {
					updateTips("상태설정이 잘 못 되었습니다.");
					return false;
				} else {
					return true;
				}	
			} else {
				updateTips("상태설정이 잘 못 되었습니다.");
				return false;
			}
		}

		$("#dialog-form").dialog({
			autoOpen:false,
			height:350,
			width:400,
			modal:true,
			buttons:{
				"저장":function() {
					var bValid = true;
					allFields.removeClass("ui-state-error");

					bValid = bValid && checkLength(title, "제목", 3, 50);
					bValid = bValid && checkLength(version, "버젼", 1, 5);

					//bValid = bValid && checkRegexp(title, /^[ㄱ-ㅎ|ㅏ-ㅣ|가-힣][a-z]([0-9a-z_])+$/i, "제목은 한글, a-z, 0-9, 언더라인으로 구성돼며 문자로 시작하여야 합니다.");
					bValid = bValid && checkRegexp(version, /^[0-9]+$/i, "버젼은 숫자만 가능합니다.");
					
					var id = $(':radio[name="masterId"]:checked').val();
					var rowData;
					if(id != undefined) {
						rowData = $("#masterGrid").getRowData(id);
					}					
					
					bValid = bValid && checkStatus(rowData.status, status.val());
					
					if(bValid) {
                        dataString = $("#masterForm").serialize();
                        $.ajax({
                            type:"POST",
                            url:_requestPath+"/admin/saveMaster",
                            data:dataString,
                            dataType:"json",
                            success:function(data, textStatus, jqXHR) {
                            	if(!data.success) {
                            	    alert(data.message);       	
                            	} else {
	                            	$("#id").val(data.data.id);
	                            	$("#masterGrid").jqGrid().trigger("reloadGrid");
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
				allFields.val( "" ).removeClass( "ui-state-error" );
			}
		});

		$("#addMaster").button().click(function() {
			  $("#dialog-form").dialog("open");
		});
		
		$("#modifyMaster").button().click(function() {
			var id = $(':radio[name="masterId"]:checked').val();
			if(id != undefined) {
				var rowData = $("#masterGrid").getRowData(id);
				$("#id").val(id);
				$("#title").val(rowData.titleText);
				$("#version").val(rowData.version);
				if(rowData.active == 'true') {
					$("#active").attr("checked", true);
				}
				
				$("#status > option[value = " + rowData.status + "]").attr("selected", "ture");
				
				$("#dialog-form").dialog("open");
			} else {
				alert("문진을 선택아여 주십시오!");
			}
		});
		
		$("#copyMaster").button().click(function() {
			
		});
	});
    </script>   
</head>
<body>
    <table id="masterGrid"></table>
    <div align="right">
	    <button id="addMaster">추가</button>
	    <button id="modifyMaster">수정</button>
	    <!--button id="copyMaster">복사</button-->
    </div>

	<div id="dialog-form" title="문진관리">
		<p class="validateTips"></p>
		<form id="masterForm">
		<fieldset>
			<input type="hidden" name="id" id="id"/>	
			<label for="title">제목</label>
			<input type="text" name="title" id="title" class="text ui-widget-content ui-corner-all" />
			<label for="version">버젼</label>
			<input type="text" name="version" id="version" class="text ui-widget-content ui-corner-all" />
			<input type="hidden" name="active" id="active" value="1" class="ui-widget-content ui-corner-all" />
			<label for="status">상태</label>
			<select name="status" id="status">
				<c:forEach var="status" items="${statuses}">
				<option value="${status}">${status}
			 	</c:forEach>			
			</select>
		</fieldset>
		</form>
	</div>    
</body>
</html>