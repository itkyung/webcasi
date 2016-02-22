<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="question checkbox">
	<div class="question-title">
		${question.questionNo}. ${question.title}
	</div>
	<c:set var="rowClass" value="checkbox-row"/>
	<c:if test="${question.type eq 'CHECK_SUBJ_RADIO_SUBJ'}">
		<c:set var="rowClass" value="checkbox-row-non-float"/>
	</c:if>
	<div class="question-item-container">
		<c:set var="resultItems" value="${resultMap[question.id]}"/>
		<c:forEach var="item" items="${question.childItems}">
			<div class="question-item-row ${rowClass}">
			<c:choose>
				<c:when test="${empty resultItems}">
					<span class="check-subj"><input questionId="${question.id}" id="${item.id}" itemId="${item.id}" type="checkbox" value="${item.id}" existChild="${item.existChildQuestion}" itemType="${item.type}">${item.title}</span>
					<c:if test="${item.type eq 'CHECK_SUBJ'}">
						<span class="check-subj-input">
							<c:if test="${!empty item.preText}">
								${item.preText}
							</c:if>
							<input type=text questionId="${question.id}" itemId="${item.id}" itemType="${item.type}" size=5 validator="${item.validator}">
							<c:if test="${!empty item.postText}">
								${item.postText}
							</c:if>
						</span>
					</c:if>
					<c:if test="${item.type eq 'CHECK_SUBJ_SUBJ'}">
						<span class="check-subj-input2">
							<c:if test="${!empty item.preText2}">
								${item.preText2}
							</c:if>
							<input type=text questionId="${question.id}" itemId="${item.id}" itemType="${item.type}" size=15 subj2="true" >
							<c:if test="${!empty item.postText2}">
								${item.postText2}
							</c:if>
						</span>					
					
						<span class="check-subj-input">
							<c:if test="${!empty item.preText}">
								${item.preText}
							</c:if>
							<input type=text questionId="${question.id}" itemId="${item.id}" itemType="${item.type}" size=5 subj1="true" validator="${item.validator}">
							<c:if test="${!empty item.postText}">
								${item.postText}
							</c:if>
						</span>
					</c:if>					
					<c:if test="${item.type eq 'CHECK_SUBJ_1'}">
						<span class="check-subj-input">
							<c:if test="${!empty item.preText}">
								${item.preText}
							</c:if>
							<input type=text questionId="${question.id}" itemId="${item.id}" itemType="${item.type}" size=10 validator="${item.validator}">
							<c:if test="${!empty item.postText}">
								${item.postText}
							</c:if>
						</span>
					</c:if>
					<span class="question-submitted-flag"><a href="javascript:clickCompleted('${item.id}','${item.existChildQuestion}');" id="${item.id}_submitted"></a></span>
				</c:when>
				<c:otherwise>
					<c:set var="innerItem" value="${resultItems[item.id]}"/>
					<c:choose>
						<c:when test="${empty innerItem}">
							<span class="check-subj"><input questionId="${question.id}" id="${item.id}" type="checkbox" value="${item.id}" existChild="${item.existChildQuestion}" itemType="${item.type}">${item.title}</span>
							<c:if test="${item.type eq 'CHECK_SUBJ'}">
								<span class="check-subj-input">
									<c:if test="${!empty item.preText}">
										${item.preText}
									</c:if>
									<input type=text questionId="${question.id}" itemId="${item.id}"  itemType="${item.type}" size=5 validator="${item.validator}">
									<c:if test="${!empty item.postText}">
										${item.postText}
									</c:if>
								</span>
							</c:if>
							<c:if test="${item.type eq 'CHECK_SUBJ_SUBJ'}">
								<span class="check-subj-input2">
									<c:if test="${!empty item.preText2}">
										${item.preText2}
									</c:if>
									<input type=text questionId="${question.id}" itemId="${item.id}" itemType="${item.type}" size=15 subj2="true">
									<c:if test="${!empty item.postText2}">
										${item.postText2}
									</c:if>
								</span>					
							
								<span class="check-subj-input">
									<c:if test="${!empty item.preText}">
										${item.preText}
									</c:if>
									<input type=text questionId="${question.id}" itemId="${item.id}" itemType="${item.type}" size=5 subj1="true" validator="${item.validator}">
									<c:if test="${!empty item.postText}">
										${item.postText}
									</c:if>
								</span>
							</c:if>		
							<c:if test="${item.type eq 'CHECK_SUBJ_1'}">
								<span class="check-subj-input">
									<c:if test="${!empty item.preText}">
										${item.preText}
									</c:if>
									<input type=text questionId="${question.id}" itemId="${item.id}" itemType="${item.type}" size=10 validator="${item.validator}">
									<c:if test="${!empty item.postText}">
										${item.postText}
									</c:if>
								</span>
							</c:if>							
							<span class="question-submitted-flag"><a href="javascript:clickCompleted('${item.id}','${item.existChildQuestion}');" id="${item.id}_submitted"></a></span>
						</c:when>
						<c:otherwise>
						<!-- 이미 답변을 선택한 경우임 -->
							<span class="check-subj"><input questionId="${question.id}" id="${item.id}" type="checkbox" value="${item.id}" existChild="${item.existChildQuestion}" itemType="${item.type}" checked>${item.title}</span>
							<c:if test="${item.type eq 'CHECK_SUBJ'}">
								<span class="check-subj-input">
									<c:if test="${!empty item.preText}">
										${item.preText}
									</c:if>
									<input type=text questionId="${question.id}" itemId="${item.id}"  value="${innerItem.strValue}" itemType="${item.type}" size=5 validator="${item.validator}">
									<c:if test="${!empty item.postText}">
										${item.postText}
									</c:if>
								</span>
							</c:if>
							<c:if test="${item.type eq 'CHECK_SUBJ_SUBJ'}">
								<span class="check-subj-input2">
									<c:if test="${!empty item.preText2}">
										${item.preText2}
									</c:if>
									<input type=text questionId="${question.id}" itemId="${item.id}" value="${innerItem.strValue2}" itemType="${item.type}" size=15 subj2="true">
									<c:if test="${!empty item.postText2}">
										${item.postText2}
									</c:if>
								</span>					
							
								<span class="check-subj-input">
									<c:if test="${!empty item.preText}">
										${item.preText}
									</c:if>
									<input type=text questionId="${question.id}" itemId="${item.id}" value="${innerItem.strValue}" itemType="${item.type}" size=5 subj1="true" validator="${item.validator}">
									<c:if test="${!empty item.postText}">
										${item.postText}
									</c:if>
								</span>
							</c:if>		
							<c:if test="${item.type eq 'CHECK_SUBJ_1'}">
								<span class="check-subj-input">
									<c:if test="${!empty item.preText}">
										${item.preText}
									</c:if>
									<input type=text questionId="${question.id}" itemId="${item.id}" value="${innerItem.strValue}" itemType="${item.type}" size=10 validator="${item.validator}">
									<c:if test="${!empty item.postText}">
										${item.postText}
									</c:if>
								</span>
							</c:if>							
							<span class="question-submitted-flag"><a href="javascript:clickCompleted('${item.id}','${item.existChildQuestion}');" id="${item.id}_submitted">답변완료</a></span>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
			</c:choose>
			</div>
		</c:forEach>
		<!-- 질문에 답변을 했는지 여부를 체크해놓는다. -->
		<c:choose>
			<c:when test="${empty resultItems}">
				<input type=hidden id="complete_flag_${question.id}" value="false"/>
			</c:when>
			<c:otherwise>
				<input type=hidden id="complete_flag_${question.id}" value="true"/>
			</c:otherwise>
		</c:choose>
	</div>
</div>