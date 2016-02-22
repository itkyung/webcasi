
$(function() {
	$("#parentItems").multiselect({ 
		   header: false,
		   selectedList: 1
	});
	
	$("#parentItems").bind("multiselectclick", function(event, ui){
		if(ui.value == $("#parentItemId").val()) {
			event.preventDefault();
		}
	});
	
	var params = {};
	params.groupId = $("#parentGroupId").val();
	params.questionId = $("#parentQuestionId").val();
	params.itemId = $("#parentItemId").val();
	params.depth = $("#depth").val();
	$("#questionGrid").jqGrid({
		url:_requestPath+'/admin/getQuestionListData',
		datatype:'json',
		mtype:'POST',
		postData:params,
		height: 200,
		rowNum:-1,
		//width: 800,
		//shrinkToFit:false,
		jsonReader:{repeatitems: false},
		autowidth:true,
	   	colNames:['id','번호','유형', '질문', '성별','등록일','수정일','활성',''],
	   	colModel:[
	   		{name:'id',index:'id', width:30, sortable : false, key:true},//, hidden:true
	   		{name:'questionNo',index:'questionNo', width:20, align:"center", sortable : false},//,formatter: customDateFormat   ,formatter:'date', formatoptions: { newformat: 'Y/m/d'}
	   		{name:'type',index:'type', width:80, sortable : false,
	   			formatter: function (cellValue, option) {
	   				var text = "";
	   				$("#type option").each(function (){
	   					if($(this).val() == cellValue) {
	   						text = $(this).text();
	   						return false;
	   					}
	   				});
	   				return $.trim(text);
				}
	   		},
	   		{name:'title',index:'title', width:230, sortable : false, formatter:viewLink},
	   		{name:'gender',index:'gender', width:20, align:"center", sortable : false
	   			/*
	   			,
	   			formatter: function (cellValue, option) {
					var ret = "전체";
					if(cellValue == "MALE") {
						ret = "남성";
					} else if(cellValue == "FEMALE") {
						ret = "여성";
					}
				    return ret;
				}
				*/
	   		},		
	   		{name:'createDate',index:'createDate', width:35, sortable:false, formatter:customDateFormat, align:"center"},
	   		{name:'updateDate',index:'updateDate', width:35, sortable:false, formatter:customDateFormat, align:"center"},
	   		{name:'active',index:'active', width:20, hidden:true, align:"center", sortable : false},		
	   		{name:'act', index:'act', width:45, sortable:false, align:"center"}
	   	],
	   	gridComplete: function(){ 
	        var ids = $("#questionGrid").getDataIDs(); 
	        for(var i=0;i<ids.length;i++){
	            var cl = ids[i];
	            be = "<input style='height:22px;width:40px;' type='button' value='Edit' onclick=editRow('"+cl+"'); />"; 
	            se = "<input style='height:22px;width:40px;' type='button' value='Del' onclick=deleteRow('"+cl+"'); />"; 
	            $("#questionGrid").setRowData(ids[i],{act:be+"&nbsp;"+se});
	        }
	    },
	    onSelectRow:function(id) {
	    	var rowData = $(this).jqGrid('getRowData',id);
	    	var itemGrid = $("#itemGrid");
	    	itemGrid.setCaption(rowData.title);
	    	itemGrid.clearGridData();
	    	var params = {};
			params.questionId = id;
			//질문 선택시 하위에 parentQuestionId설정.
			$('#parentQuestionId-item').val(id);
			itemGrid.setGridParam({postData:params,url:_requestPath+'/admin/getItemListData'});
			itemGridReLoad();
		},
	   	emptyrecords:"자료가 없습니다.",
	   	caption: "질문"
	});
	
	$("#itemGrid").jqGrid({
		datatype:'json',
		mtype:'POST',
		postData:{},
		height: 150,
		rowNum:-1,
		//width: 800,
		//shrinkToFit:false,
		jsonReader:{repeatitems: false},
		autowidth:true,
	   	colNames:['id','유형', '답변', '하위질문', '등록일','수정일','활성',''],
	   	colModel:[
	   		{name:'id',index:'id', width:30, hidden:true, sortable : false, key:true},
	   		{name:'type',index:'type', width:80, sortable : false,
	   			formatter: function (cellValue, option) {
	   				var text = "";
	   				$("#type-item option").each(function (){
	   					if($(this).val() == cellValue) {
	   						text = $(this).text();
	   						return false;
	   					}
	   				});
	   				return $.trim(text);
				}
	   		},
	   		{name:'title',index:'title', width:300, sortable : false, formatter:viewItemLink},
	   		{name:'existChildQuestion',index:'existChildQuestion', width:40, align:"center", sortable : false},		
	   		{name:'createDate',index:'createDate', width:50, sortable:false, formatter:customDateFormat, align:"center"},
	   		{name:'updateDate',index:'updateDate', width:50, sortable:false, formatter:customDateFormat, align:"center"},
	   		{name:'active',index:'active', width:20, hidden:true, align:"center", sortable : false},
	   		{name:'act', index:'act', width:60, sortable:false, align:"center"}
	   	],
	   	gridComplete: function(){ 
	        var ids = $("#itemGrid").getDataIDs(); 
	        for(var i=0;i<ids.length;i++){
	            var cl = ids[i];
	            be = "<input style='height:22px;width:40px;' type='button' value='Edit' onclick=editItemRow('"+cl+"'); />"; 
	            se = "<input style='height:22px;width:40px;' type='button' value='Del' onclick=deleteItemRow('"+cl+"'); />"; 
	            $("#itemGrid").setRowData(ids[i],{act:be+"&nbsp;"+se});
	        }
	    }, 
	   	emptyrecords:"자료가 없습니다.",
	   	caption: "답변"
	});
	
	
	$( "#dialog-form" ).dialog({
      autoOpen: false,
      height: 590,
      width: 680,
      modal: true,
      buttons: {
        "저장": function() {
        	changeNameNurseItems();
        	$("#questionForm").submit();
        },
        "닫기": function() {
          $( this ).dialog( "close" );
        }
      },
      close: function() {
    	  $("#questionForm").resetForm();
    	  $("#viewFile").empty();
      	  $("#nurse-item").empty();
    	  $('#editor').empty();
    	  $("#required").attr("checked", false);
    	  $("#navigationStressFlag").attr("checked", false);
    	  $("#navigationNutritionFlag").attr("checked", false);
    	  $("#checkListRequired").attr("checked", false);
      }
    });
 
	$( "#dialog-form-item" ).dialog({
	      autoOpen: false,
	      height: 500,
	      width: 650,
	      modal: true,
	      buttons: {
	        "저장": function() {
	        	changeNameChildItems();
	        	$("#questionItemForm").submit();
	        },
	        "닫기": function() {
	          $( this ).dialog( "close" );
	        }
	      },
	      close: function() {
	    	  var pqId = $('#parentQuestionId-item').val();
	    	  $("#questionItemForm").resetForm();
	    	  $("#content-item").empty();
	    	  $("#noneFlag-item").attr("checked", false);
	    	// 선택된 질문의 id값을 다시 넣어준다.
	    	  $('#parentQuestionId-item').val(pqId);
	      }
	    });
	
    $( "button" ).button().click(function( event ) {
      event.preventDefault();
    });
    
    $( "#create-question" ).button().click(function() {
    	$("#questionId").val('');
    	createEditor();
    	$( "#dialog-form" ).dialog( "open" );
    });
    
    $( "#create-item" ).button().click(function() {
    	if($('#parentQuestionId-item').val() == "") {
    		alert("질문을 먼저 선택해 주세요.");
    		return false;
    	}
    	$("#itemId-item").val('');
    	$( "#dialog-form-item" ).dialog( "open" );
    });
    
    $( "#list" ).button().click(function() {
    	var depth = $("#depth").val();
    	var groupId = $("#searchGroupId").val();
    	if(depth == 1){
    		document.location.href = _requestPath + "/admin/questionGroupList?masterId=" + $("#searchMasterId").val()+"&categoryType=" + $("#searchCategoryType").val();
    	} else if(depth == 2){
    		document.location.href = _requestPath + "/admin/questionList?groupId=" + groupId+"&depth=1";
    	} else {
    		document.location.href = _requestPath + "/admin/questionList?groupId=" + groupId+"&questionId=" + $("#searchQuestionId").val()+"&itemId=" + $("#searchItemId").val()+"&depth=2";
    	}
    	
    });
    
    $('#questionForm').ajaxForm({
    	dataType:'json',
    	success : function(result){
			var success = result.success;
			if(success){
				gridReLoad();
				$( "#dialog-form" ).dialog( "close" );
			}else{
				alert(result.msg);
			}
		},
		error : function(response, status, err){
			alert(err);
		}
    });
    
    $('#questionItemForm').ajaxForm({
    	dataType:'json',
    	success : function(result){
			var success = result.success;
			if(success){
				itemGridReLoad();
				$( "#dialog-form-item" ).dialog( "close" );
			}else{
				alert(result.msg);
			}
		},
		error : function(response, status, err){
			alert(err);
		}
    });
    
    $( "#itemObjAdd-item" ).button().click(function(event) {	
  	  event.preventDefault();
  	  addItemObjRow();
    });
    
    $( "#nurseItemAdd" ).button().click(function(event) {	
    	event.preventDefault();
    	addNurseItemRow();
    });
    
    
    //임시
    $( "#copy-item" ).button().click(function() {
    	addItemDetailCopy('');
    });
    $( "#copy-Question" ).button().click(function() {
    	addQuestionlCopy();
    });
    
    
});

function createEditor() {
	$('#editor').append("<textarea name='contents' id='contents' ></textarea>");
	$('#contents').jqte();
}

function addNurseItemRow() {
	var rows = $("#nurse-item li");
	var index = rows.length;
   
	$("#nurse-item").append(
		"<li id='nurseItem"+index+"'>"
		+"키<input type='text' name='childNurseItems[].key' size=5 />&nbsp;"
		+"값<input type='text' name='childNurseItems[].value' size=5 />&nbsp;"
		+"AskCode<input type='text' name='childNurseItems[].ocsAskCode' size=5/>&nbsp;"
		+"Answer<input type='text'  name='childNurseItems[].ocsAnswer' size=5/>&nbsp;"
		+"NoAnswer<input type='text'  name='childNurseItems[].ocsNoAnswerCode' size=5/>&nbsp;"
		+" <a href='javascript:deleteNurseItemRow("+index+");'>삭제</a>"
		+"</li>"
	);
}

function deleteNurseItemRow(index) {
 var row = $("#nurseItem"+index);
 row.remove();
}

function changeNameNurseItems() {
	$("#nurse-item li").each(function(index) {
		$(this).find("input").each(function () {
			var name = $(this).attr("name");
			name = name.replace("[]", "[" +index + "]");
			$(this).attr("name",name);
		});
	});
}

function addItemObjRow() {
	var rows = $("#content-item li");
	var index = rows.length;
   
	$("#content-item").append(
		"<li id='item"+index+"'>"
		+"키<input type='text' name='childItems[].key' size=5 />&nbsp;"
		+"값<input type='text' name='childItems[].value' size=5 />&nbsp;"
		+"AskCode<input type='text' name='childItems[].ocsAskCode' size=5/>&nbsp;"
		+"Answer<input type='text'  name='childItems[].ocsAnswer' size=5/>&nbsp;"
		+"NoAnswer<input type='text'  name='childItems[].ocsNoAnswerCode' size=5/>&nbsp;"
		+" <a href='javascript:deleteItemObjRow("+index+");'>삭제</a>"
		+"</li>"
	);
}

function deleteItemObjRow(index) {
	var row = $("#item"+index);
	row.remove();
}

function changeNameChildItems() {
	$("#content-item li").each(function(index) {
		$(this).find("input").each(function () {
			var name = $(this).attr("name");
			name = name.replace("[]", "[" +index + "]");
			$(this).attr("name",name);
		});
	});
}

function customDateFormat(cellValue, option) {
	if(cellValue){
		return $.datepicker.formatDate('yy-mm-dd', new Date(cellValue));
	}
	return "";
}

function gridReLoad() {
	$("#questionGrid").jqGrid().trigger("reloadGrid");
}

function itemGridReLoad() {
	$("#itemGrid").jqGrid().trigger("reloadGrid");
}

function editRow(id) {
	$.ajax({
		dataType:  'json', 
		type : 'POST',
		url : _requestPath + '/admin/getQuestion',
		//timeout : 5000,
		data : {questionId:id},
		beforeSubmit : function(){
			
		},				
		success : function(result){
			var success = result.success;
			if(success){
				setForm(result.question);
				$( "#dialog-form" ).dialog( "open" );
			}else{
				alert(result.msg);
			}
		},
		error : function(response, status, err){
			alert(err);
			
		}
	});
}

function editItemRow(id) {
	$.ajax({
		dataType:  'json', 
		type : 'POST',
		url : _requestPath + '/admin/getQuestionItem',
		//timeout : 5000,
		data : {itemId:id},
		beforeSubmit : function(){
			
		},				
		success : function(result){
			var success = result.success;
			if(success){
				setItemForm(result.item);
				$( "#dialog-form-item" ).dialog( "open" );
			}else{
				alert(result.msg);
			}
		},
		error : function(response, status, err){
			alert(err);
			
		}
	});
}

function setForm(data) {
	$("#viewFile").empty();
	createEditor();
	if(data.help != null) {
		$("#helpId").val(data.help.id);

		$('#editor').empty();
		$('#editor').append("<textarea name='contents' id='contents' ></textarea>");
		$('#contents').val(data.help.contents);
		$('#contents').jqte();
		 var fileName = data.help.attachFilePath;
		 if(fileName != null && fileName != "") {
			 //$("#viewFile").append("<a href=\""+_requestPath+"/filedownload?helpId="+data.help.id+"\">"+fileName.substring(fileName.indexOf("_")+1)+"</a>");
			 $("#viewFile").append(fileName.substring(fileName.indexOf("_")+1));
		 }
	}
	$("#questionId").val(data.id);
	$("#questionNo").val(data.questionNo);
	$("#ocsAskCode").val(data.ocsAskCode);
	$("#ocsNoAnswerCode").val(data.ocsNoAnswerCode);
	$("#ocsAskCode2").val(data.ocsAskCode2);
	$("#ocsNoAnswerCode2").val(data.ocsNoAnswerCode2);
	$("#ocsAskCode3").val(data.ocsAskCode3);
	$("#ocsNoAnswerCode3").val(data.ocsNoAnswerCode3);
	$("#type").val(data.type);
	$("#title").val(data.title);
	$("#description").val(data.description);
	$("#gender").val(data.gender);
	$("#validator").val(data.validator);
	$("#minRange").val(data.minRange);
	$("#maxRange").val(data.maxRange);
	$("#preText").val(data.preText);
	$("#postText").val(data.postText);
	
	if(data.required) $("#required").attr("checked", true);
	if(data.navigationStressFlag) $("#navigationStressFlag").attr("checked", true);
	if(data.navigationNutritionFlag) $("#navigationNutritionFlag").attr("checked", true);
	
	var select = $('#parentItems');
	select.multiselect("uncheckAll");
	for(var i=0 ; i < data.parentItems.length ; i++) {
		select.find('option[value=' + data.parentItems[i] + ']').attr("selected", "selected");
		select.multiselect("refresh");
	}
	
	if(data.checkListRequired == true) $("#checkListRequired").attr("checked", true);
	$("#nurseQuestionType").val(data.nurseQuestionType);
	
	$("#nurse-item").empty();
	for(var i=0 ; i < data.childNurseItems.length ; i++) {
		
		var key = data.childNurseItems[i].key == null ? "" : data.childNurseItems[i].key;
		var value = data.childNurseItems[i].value == null ? "" : data.childNurseItems[i].value;
		var ocsAskCode = data.childNurseItems[i].ocsAskCode == null ? "" : data.childNurseItems[i].ocsAskCode;
		var ocsAnswer = data.childNurseItems[i].ocsAnswer == null ? "" : data.childNurseItems[i].ocsAnswer;
		var ocsNoAnswerCode = data.childNurseItems[i].ocsNoAnswerCode == null ? "" : data.childNurseItems[i].ocsNoAnswerCode;
			
		$("#nurse-item").append(
			"<li id='nurseItem"+i+"'>"
			+"키<input type='text' name='childNurseItems[].key' value=\'" + key + "\' size=5 />&nbsp;"
			+"값<input type='text' name='childNurseItems[].value' value=\'" + value + "\' size=5 />&nbsp;"
			+"AskCode<input type='text' name='childNurseItems[].ocsAskCode' value=\'" + ocsAskCode + "\' size=5/>&nbsp;"
			+"Answer<input type='text'  name='childNurseItems[].ocsAnswer' value=\'" + ocsAnswer + "\' size=5/>&nbsp;"
			+"NoAnswer<input type='text'  name='childNurseItems[].ocsNoAnswerCode' value=\'" + ocsNoAnswerCode + "\' size=5/>&nbsp;"
			+" <a href='javascript:deleteNurseItemRow("+i+");'>삭제</a>"
			+"</li>"
		);
	}
	
	$("#nurseOcsAnswer").val(data.nurseOcsAnswer);
	$("#nurseOcsAskCode").val(data.nurseOcsAskCode);
	$("#nurseOcsNoAnswerCode").val(data.nurseOcsNoAnswerCode);
	
	$("#sortOrder").val(data.sortOrder);
	
}

function setItemForm(data) {
	$("#itemId-item").val(data.id);
	$("#type-item").val(data.type);
	$("#ocsAnswer-item").val(data.ocsAnswer);
	$("#title-item").val(data.title);
	
	$("#ocsAskCode-item").val(data.ocsAskCode);
	$("#ocsNoAnswerCode-item").val(data.ocsNoAnswerCode);
	$("#ocsAskCode2-item").val(data.ocsAskCode2);
	$("#ocsNoAnswerCode2-item").val(data.ocsNoAnswerCode2);
	$("#ocsAskCode3-item").val(data.ocsAskCode3);
	$("#ocsNoAnswerCode3-item").val(data.ocsNoAnswerCode3);
	$("#preText-item").val(data.preText);
	$("#postText-item").val(data.postText);
	$("#ocsAskCode2-item").val(data.ocsAskCode2);
	$("#preText2-item").val(data.preText2);
	$("#postText2-item").val(data.postText2);
	$("#validator-item").val(data.validator);
	$("#minRange-item").val(data.minRange);
	$("#maxRange-item").val(data.maxRange);
	
	$("#thumnailImage-item").val(data.thumnailImage);
	$("#itemGroup-item").val(data.itemGroup);
	
	if(data.noneFlag) $("#noneFlag-item").attr("checked", true);
	
	//alert(data.childItems.length);
	
	$("#content-item").empty();
	for(var i=0 ; i < data.childItems.length ; i++) {
		
		var key = data.childItems[i].key == null ? "" : data.childItems[i].key;
		var value = data.childItems[i].value == null ? "" : data.childItems[i].value;
		var ocsAskCode = data.childItems[i].ocsAskCode == null ? "" : data.childItems[i].ocsAskCode;
		var ocsAnswer = data.childItems[i].ocsAnswer == null ? "" : data.childItems[i].ocsAnswer;
		var ocsNoAnswerCode = data.childItems[i].ocsNoAnswerCode == null ? "" : data.childItems[i].ocsNoAnswerCode;
			
		$("#content-item").append(
			"<li id='item"+i+"'>"
			+"키<input type='text' name='childItems[].key' value=\'" + key + "\' size=5 />&nbsp;"
			+"값<input type='text' name='childItems[].value' value=\'" + value + "\' size=5 />&nbsp;"
			+"AskCode<input type='text' name='childItems[].ocsAskCode' value=\'" + ocsAskCode + "\' size=5/>&nbsp;"
			+"Answer<input type='text'  name='childItems[].ocsAnswer' value=\'" + ocsAnswer + "\' size=5/>&nbsp;"
			+"NoAnswer<input type='text'  name='childItems[].ocsNoAnswerCode' value=\'" + ocsNoAnswerCode + "\' size=5/>&nbsp;"
			+" <a href='javascript:deleteItemObjRow("+i+");'>삭제</a>"
			+"</li>"
		);
	}
	
	$("#sortOrder-item").val(data.sortOrder);
}

function deleteRow(id) {
	if(confirm("삭제하시겠습니까?")) {
		$.ajax({
			dataType:  'json', 
			type : 'POST',
			url : _requestPath + '/admin/questionDelete',
			//timeout : 5000,
			data : {questionId:id},
			beforeSubmit : function(){
				
			},				
			success : function(result){
				var success = result.success;
				if(success){
					gridReLoad();
					$( "#dialog-form" ).dialog( "close" );
				}else{
					alert(result.msg);
				}
			},
			error : function(response, status, err){
				alert(err);
			}
		});
	}
}

function deleteItemRow(id) {
	if(confirm("삭제하시겠습니까?")) {
		$.ajax({
			dataType:  'json', 
			type : 'POST',
			url : _requestPath + '/admin/questionItemDelete',
			//timeout : 5000,
			data : {itemId:id},
			beforeSubmit : function(){
				
			},				
			success : function(result){
				var success = result.success;
				if(success){
					itemGridReLoad();
					$( "#dialog-form-item" ).dialog( "close" );
				}else{
					alert(result.msg);
				}
			},
			error : function(response, status, err){
				alert(err);
			}
		});
	}
}

function viewLink(cellValue, options, rowObject) {
	var url = "";
	if(cellValue != null && $("#depth").val() != 3 && rowObject.type == "TAB") {
		url = '<a href="javascript:goQuestion(\''+rowObject.id+'\');"><font color=blue>'+cellValue+'</font></a>';
	} else {
		url = cellValue;
	}
    return url;
}
function goQuestion(id){
	var depth = Number($("#depth").val()) + 1;
	var groupId = $("#searchGroupId").val();
	var searchQuestionId = $("#parentQuestionId").val();
	var searchItemId = $("#parentItemId").val();
	document.location.href = _requestPath + "/admin/questionList?groupId=" + groupId+"&questionId=" + id+"&depth="+depth+"&searchQuestionId="+searchQuestionId+"&searchItemId="+searchItemId;
}
function viewItemLink(cellValue, options, rowObject) {
	var url = "";
	if(cellValue != null && $("#depth").val() != 3) {
		url = '<a href="javascript:goItem(\''+rowObject.id+'\');"><font color=blue>'+cellValue+'</font></a>';
	} else {
		url = cellValue;
	}
    return url;
}
function goItem(id){
	var depth = Number($("#depth").val()) + 1;
	var groupId = $("#searchGroupId").val();
	var searchQuestionId = $("#parentQuestionId").val();
	var searchItemId = $("#parentItemId").val();
	document.location.href = _requestPath + "/admin/questionList?groupId=" + groupId+"&itemId=" + id+"&depth="+depth+"&searchQuestionId="+searchQuestionId+"&searchItemId="+searchItemId;
}







//임시
function addItemDetailCopy(questionId) {
	
	if(questionId != "") {
		$('#parentQuestionId-item').val(questionId);
	}
	
	if($('#parentQuestionId-item').val() == "") {
		alert("질문을 먼저 선택해 주세요.");
		return false;
	}	
	if($('#copyQuestionId').val() == "") {
		alert("복사할 질문 ID를 입력해 주세요.");
		return false;
	}
	
	var parentQuestionId = $('#parentQuestionId-item').val();
	var copyQuestionId = $('#copyQuestionId').val();
	
	$.ajax({
		dataType:  'json', 
		type : 'POST',
		url : _requestPath + '/admin/questionItemCopy',
		//timeout : 5000,
		data : {parentQuestionId:parentQuestionId, copyQuestionId:copyQuestionId},
		beforeSubmit : function(){
			
		},				
		success : function(result){
			var success = result.success;
			if(success){
				var id = $('#parentQuestionId-item').val();
				var rowData = $("#questionGrid").jqGrid('getRowData',id);
		    	var itemGrid = $("#itemGrid");
		    	itemGrid.setCaption(rowData.title);
		    	itemGrid.clearGridData();
		    	var params = {};
				params.questionId = id;
				//질문 선택시 하위에 parentQuestionId설정.
				itemGrid.setGridParam({postData:params,url:_requestPath+'/admin/getItemListData'});
				itemGridReLoad();
			}else{
				alert(result.msg);
			}
		},
		error : function(response, status, err){
			alert(err);
		}
	});
}


//임시
function addQuestionlCopy() {
	
	if($('#copyQuestionId').val() == "") {
		alert("복사할 질문 ID를 입력해 주세요.");
		return false;
	}
	
	var param = {};
	param.parentGroupId = $('#parentGroupId').val();
	param.parentQuestionId = $('#parentQuestionId').val();
	param.parentItemId = $('#parentItemId').val();
	param.depth = $('#depth').val();
	param.copyQuestionId = $('#copyQuestionId').val();
		
		
	$.ajax({
		dataType:  'json', 
		type : 'POST',
		url : _requestPath + '/admin/questionCopy',
		//timeout : 5000,
		data : param,
		success : function(result){
			var success = result.success;
			if(success){
				gridReLoad();
				addItemDetailCopy(result.questionId);
			}else{
				alert(result.msg);
			}
		},
		error : function(response, status, err){
			alert(err);
		}
	});
}
