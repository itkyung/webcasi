<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<div class="question checkbox">
	<c:if test="${!empty question.description}">
		<div class="question-short-desc">
			${question.help.contents}
		</div>
	</c:if>
	<c:set var="cssPostfix" scope="request">
		<c:choose>
			<c:when test="${questionIndex eq 0 || questionIndex % 2 eq 0}">
				<c:out value="even"/>
			</c:when>
			<c:otherwise>
				<c:out value="odd"/>
			</c:otherwise>
		</c:choose>
	</c:set>
	<div class="question-title-top ${cssPostfix}">
		<div class="question-title-left"></div>
		<div class="question-title-center"></div>
		<div class="question-title-right"></div>
	</div>
	
	<div class="question-title ${cssPostfix}" id="question-${question.id}">
		<c:choose>
			<c:when test="${empty questionNo}">
				${question.questionNo}.  ${question.title}
			</c:when>
			<c:otherwise>
				${questionNo}.  ${question.title}
			</c:otherwise>
		</c:choose>	
	</div>

	<c:set var="rowClass" value="checkbox-row"/>
	<c:set var="checkClass" value="check-subj"/>
	<c:if test="${question.type eq 'CHECK_SUBJ_RADIO_SUBJ'}">
		<c:set var="rowClass" value="checkbox-row-non-float"/>
	</c:if>
	<div class="question-item-container" >
		<c:set var="resultItems" value="${resultMap[question.id]}"/>
		<c:forEach var="item" items="${question.childItems}">
			<c:if test="${item.active eq true}">
				<c:if test="${!empty vertical}">
					<c:set var="rowClass" value="checkbox-long-row"/>
					<c:set var="checkClass" value="check-subj-long"/>
				</c:if>
				<c:if test="${(item.type eq 'CHECK_SUBJ_SUBJ') || (item.type eq 'CHECK_SUBJ_1') || (item.type eq 'CHECK_VER')}">
					<c:set var="rowClass" value="checkbox-long-row"/>
				</c:if>
				<c:if test="${item.type eq 'CHECK_SUBJ_RADIO_SUBJ'}">
					<c:set var="rowClass" value="checkbox-very-long-row"/>
				</c:if>
				<c:if test="${question.type ne 'CHECK_SUBJ_RADIO_SUBJ' && item.type eq 'CHECK_SUBJ' && item.validator eq 'NONE'}">
					<c:set var="rowClass" value="checkbox-long-row"/>
				</c:if>
				<c:if test="${item.type eq 'CHECK_SUBJ_RADIO_SUBJ' || item.type eq 'CHECK'}">
					<c:set var="checkClass" value="check-subj-short"/>
				</c:if>
				<c:if test="${item.type eq 'CHECK'}">
					<c:set var="rowClass" value="checkbox-row"/>
				</c:if>
				<div class="question-item-row ${rowClass}">
						<c:set var="innerItem" value="${resultItems[item.id]}"/>
						<c:choose>
							<c:when test="${empty innerItem}">
								<div class="${checkClass}">
									<input questionId="${question.id}" id="${item.id}" type="checkbox" 
										value="${item.id}" existChild="${item.existChildQuestion}" itemType="${item.type}" questionType="${question.type}" 
										noneFlag="${item.noneFlag}" tabQuestion="${tabQuestion}"><a href="javascript:clickCheckboxLink('${item.id}')">${item.title}</a>
								</div>
								<c:if test="${item.type eq 'CHECK_SUBJ'}">
									<c:set var="checkSubjTextLength" value="3"/>
									<c:set var="checkInputClass" value="check-subj-input"/>
									<c:if test="${item.validator eq 'NONE'}">
										<c:set var="checkSubjTextLength" value="10"/>
										<c:set var="checkInputClass" value="check-subj-long-input"/>
									</c:if>
									<div class="${checkInputClass}">
										<c:if test="${!empty item.preText}">
											${item.preText}
										</c:if>
										<input type=text questionId="${question.id}" itemId="${item.id}"  itemType="${item.type}" 
											size=${checkSubjTextLength} existChild="${item.existChildQuestion}" validator="${item.validator}"  minRange="${item.minRange}" maxRange="${item.maxRange}" disabled>
										<c:if test="${!empty item.postText}">
											${item.postText}
										</c:if>
									</div>
								</c:if>
								<c:if test="${item.type eq 'CHECK_SUBJ_SUBJ'}">
									<div class="check-subj-input2">
										<c:if test="${!empty item.preText2}">
											${item.preText2}
										</c:if>
										<input type=text questionId="${question.id}" itemId="${item.id}" itemType="${item.type}" size=15 subj2="true" 
											existChild="${item.existChildQuestion}" disabled>
										<c:if test="${!empty item.postText2}">
											${item.postText2}
										</c:if>
									</div>					
								
									<div class="check-subj-input">
										<c:if test="${!empty item.preText}">
											${item.preText}
										</c:if>
										<input type=text questionId="${question.id}" itemId="${item.id}" itemType="${item.type}" size=3 subj1="true" 
											existChild="${item.existChildQuestion}" validator="${item.validator}"  minRange="${item.minRange}" maxRange="${item.maxRange}" disabled>
										<c:if test="${!empty item.postText}">
											${item.postText}
										</c:if>
									</div>
								</c:if>		
								<c:if test="${item.type eq 'CHECK_SUBJ_1'}">
									<c:set var="checkSubjTextLength" value="3"/>
									<c:set var="checkInputClass" value="check-subj-input"/>
									<c:if test="${item.validator eq 'NONE'}">
										<c:set var="checkSubjTextLength" value="10"/>
										<c:set var="checkInputClass" value="check-subj-long-input"/>
									</c:if>
									<div class="${checkInputClass}">
										
										<c:if test="${!empty item.preText}">
											${item.preText}
										</c:if>
										<input type=text questionId="${question.id}" itemId="${item.id}" itemType="${item.type}" 
											size=10 validator="${item.validator}" existChild="${item.existChildQuestion}" tabQuestion="${tabQuestion}"
												 minRange="${item.minRange}" maxRange="${item.maxRange}" disabled>
										<c:if test="${!empty item.postText}">
											${item.postText}
										</c:if>
									</div>
								</c:if>			
								<c:if test="${item.type eq 'CHECK_SUBJ_RADIO_SUBJ'}">
									<c:set var="embededItemResult" value="${innerItem.objectiveValue2}"/>
									<c:forEach var="embededItem" items="${item.childItems}">
										<c:choose>
											<c:when test="${embededItem.key eq embededItemResult}">
												<c:set var="checkedFlag" value="checked"/>
											</c:when>
											<c:otherwise>
												<c:set var="checkedFlag" value=""/>
											</c:otherwise>
										</c:choose>
										<div class="embeded-item"><input id="embeded_${item.id}_${embededItem.key}" embededItemId="${embededItem.key}" itemId="${item.id}" questionId="${question.id}" type="radio" name="${item.id}" 
													value="${embededItem.key}" itemType="${item.type}" existChild="${item.existChildQuestion}" ${checkedFlag} disabled>
														<a href="javascript:clickRadioLink('embeded_${item.id}_${embededItem.key}');">${embededItem.value}</a></div>
									</c:forEach>
									<div class="check-subj-input">
										<c:if test="${!empty item.preText}">
											${item.preText}
										</c:if>
										<input type=text questionId="${question.id}" itemId="${item.id}" itemType="${item.type}" size=10 subj1="true" 
											validator="${item.validator}" existChild="${item.existChildQuestion}"  minRange="${item.minRange}" maxRange="${item.maxRange}" disabled>
										<c:if test="${!empty item.postText}">
											${item.postText}
										</c:if>
									</div>
									<div class="check-subj-input2">
										<c:if test="${!empty item.preText2}">
											${item.preText2}
										</c:if>
										<input type=text questionId="${question.id}" itemId="${item.id}" itemType="${item.type}" size=7 subj2="true" existChild="${item.existChildQuestion}" disabled>
										<c:if test="${!empty item.postText2}">
											${item.postText2}
										</c:if>
									</div>	
									
								</c:if>		
								<div class="question-submitted-flag"><a href="javascript:clickCompleted('${item.id}','${item.existChildQuestion}');" id="${item.id}_submitted" existChild="${item.existChildQuestion}"></a></div>
							</c:when>
							<c:otherwise>
							<!-- 이미 답변을 선택한 경우임 -->
								<div class="${checkClass}">
									<input questionId="${question.id}" id="${item.id}" type="checkbox" value="${item.id}" existChild="${item.existChildQuestion}" 
										itemType="${item.type}" noneFlag="${item.noneFlag}" tabQuestion="${tabQuestion}" questionType="${question.type}" checked><a href="javascript:clickCheckboxLink('${item.id}')">${item.title}</a>
								</div>
								<c:if test="${item.type eq 'CHECK_SUBJ'}">
									<c:set var="checkSubjTextLength" value="3"/>
									<c:set var="checkInputClass" value="check-subj-input"/>
									<c:if test="${item.validator eq 'NONE'}">
										<c:set var="checkSubjTextLength" value="10"/>
										<c:set var="checkInputClass" value="check-subj-long-input"/>
									</c:if>
									<div class="${checkInputClass}">
										<c:if test="${!empty item.preText}">
											${item.preText}
										</c:if>
										
										<input type=text questionId="${question.id}" itemId="${item.id}"  value="${innerItem.strValue}" itemType="${item.type}" 
											size=${checkSubjTextLength} validator="${item.validator}" existChild="${item.existChildQuestion}"  minRange="${item.minRange}" maxRange="${item.maxRange}">
										<c:if test="${!empty item.postText}">
											${item.postText}
										</c:if>
									</div>
								</c:if>
								<c:if test="${item.type eq 'CHECK_SUBJ_SUBJ'}">
									<div class="check-subj-input2">
										<c:if test="${!empty item.preText2}">
											${item.preText2}
										</c:if>
										<input type=text questionId="${question.id}" itemId="${item.id}" value="${innerItem.strValue2}" itemType="${item.type}" size=15 subj2="true" existChild="${item.existChildQuestion}">
										<c:if test="${!empty item.postText2}">
											${item.postText2}
										</c:if>
									</div>					
								
									<div class="check-subj-input">
										<c:if test="${!empty item.preText}">
											${item.preText}
										</c:if>
										<input type=text questionId="${question.id}" itemId="${item.id}" value="${innerItem.strValue}" itemType="${item.type}" size=3 subj1="true" 
											validator="${item.validator}" existChild="${item.existChildQuestion}"  minRange="${item.minRange}" maxRange="${item.maxRange}">
										<c:if test="${!empty item.postText}">
											${item.postText}
										</c:if>
									</div>
								</c:if>		
								<c:if test="${item.type eq 'CHECK_SUBJ_1'}">
									<c:set var="checkSubjTextLength" value="3"/>
									<c:set var="checkInputClass" value="check-subj-input"/>
									<c:if test="${item.validator eq 'NONE'}">
										<c:set var="checkSubjTextLength" value="10"/>
										<c:set var="checkInputClass" value="check-subj-long-input"/>
									</c:if>
									<div class="${checkInputClass}">
										<c:if test="${!empty item.preText}">
											${item.preText}
										</c:if>
										<input type=text questionId="${question.id}" itemId="${item.id}" value="${innerItem.strValue}" 
											itemType="${item.type}" size=10 validator="${item.validator}" existChild="${item.existChildQuestion}" tabQuestion="${tabQuestion}"  minRange="${item.minRange}" maxRange="${item.maxRange}">
										<c:if test="${!empty item.postText}">
											${item.postText}
										</c:if>
									</div>
								</c:if>		
								<c:if test="${item.type eq 'CHECK_SUBJ_RADIO_SUBJ'}">
									<c:set var="embededItemResult" value="${innerItem.objectiveValue2}"/>
									<c:forEach var="embededItem" items="${item.childItems}">
										<c:choose>
											<c:when test="${embededItem.key eq embededItemResult}">
												<c:set var="checkedFlag" value="checked"/>
											</c:when>
											<c:otherwise>
												<c:set var="checkedFlag" value=""/>
											</c:otherwise>
										</c:choose>
										<div class="embeded-item">
											<input id="embeded_${item.id}_${embededItem.key}" embededItemId="${embededItem.key}" itemId="${item.id}" questionId="${question.id}" type="radio" name="${item.id}" 
													value="${embededItem.key}" itemType="${item.type}" existChild="${item.existChildQuestion}" ${checkedFlag}>
											<a href="javascript:clickRadioLink('embeded_${item.id}_${embededItem.key}');">${embededItem.value}</a>
										</div>
													
									</c:forEach>
									<div class="check-subj-input">
										<c:if test="${!empty item.preText}">
											${item.preText}
										</c:if>
										<input type=text questionId="${question.id}" itemId="${item.id}" itemType="${item.type}" size=15 subj1="true" 
											validator="${item.validator}" value="${innerItem.strValue}" existChild="${item.existChildQuestion}"  minRange="${item.minRange}" maxRange="${item.maxRange}">
										<c:if test="${!empty item.postText}">
											${item.postText}
										</c:if>
									</div>
									<div class="check-subj-input2">
										<c:if test="${!empty item.preText2}">
											${item.preText2}
										</c:if>
										<input type=text questionId="${question.id}" itemId="${item.id}" itemType="${item.type}" size=10 subj2="true" value="${innerItem.strValue2}" existChild="${item.existChildQuestion}">
										<c:if test="${!empty item.postText2}">
											${item.postText2}
										</c:if>
									</div>	
									
								</c:if>							
								<div class="question-submitted-flag">
									<a href="javascript:clickCompleted('${item.id}','${item.existChildQuestion}');" id="${item.id}_submitted" existChild="${item.existChildQuestion}">
									<c:if test="${item.existChildQuestion && empty tabQuestion && empty noChild}">
										<img src="${pageContext.request.contextPath}/resources/images/bt_reply.png"/>
									</c:if>
									</a></div>
							</c:otherwise>
						</c:choose>
	
				</div>
			</c:if>
		</c:forEach>
		<!-- 질문에 답변을 했는지 여부를 체크해놓는다. -->
		<!-- 해당 질문의 required여부도 고려한다. -->
		<c:choose>
			<c:when test="${empty resultItems && question.required}">
				<input type=hidden id="complete_flag_${question.id}" questionId="${question.id}" requiredFlag="true" value="false"/>
			</c:when>
			<c:otherwise>
				<input type=hidden id="complete_flag_${question.id}" questionId="${question.id}" requiredFlag="false" value="true"/>
			</c:otherwise>
		</c:choose>
	</div>
	<div class="question-item-container-bottom">
		<div class="question-item-container-bottom-left"></div>
		<div class="question-item-container-bottom-center"></div>
		<div class="question-item-container-bottom-right"></div>
	</div>	
</div>