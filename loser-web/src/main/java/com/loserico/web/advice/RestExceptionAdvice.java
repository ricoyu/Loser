package com.loserico.web.advice;

import static com.loserico.web.enums.StatusCode.BAD_REQUEST;
import static com.loserico.web.enums.StatusCode.ENTITY_NOT_FOUND;
import static java.text.MessageFormat.format;
import static java.util.Arrays.asList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.expression.spel.ast.OpLE;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.loserico.commons.exception.ApplicationException;
import com.loserico.web.bean.ErrorMessage;
import com.loserico.web.enums.StatusCode;
import com.loserico.web.exception.AbstractPropertyExistsException;
import com.loserico.web.exception.CommonException;
import com.loserico.web.exception.EntityNotFoundException;
import com.loserico.web.exception.GeneralValidationException;
import com.loserico.web.exception.LocalizedException;
import com.loserico.web.i18n.LocaleContextHolder;
import com.loserico.web.utils.MessageHelper;
import com.loserico.web.utils.ValidationUtils;
import com.loserico.web.vo.Result;
import com.loserico.web.vo.Results;
import com.loserico.web.vo.Results.Builder;

@RestControllerAdvice
public class RestExceptionAdvice extends ResponseEntityExceptionHandler {
	private static final Logger logger = LoggerFactory.getLogger(RestExceptionAdvice.class);

	private static Pattern pattern = Pattern.compile("^Unable to find\\s*([a-zA-Z0-9_]+)\\s*with id .*");
	private static Pattern objectRetrievalFailurePattern = Pattern.compile("^Unable to find\\s*.*\\.([a-zA-Z0-9_]+)\\s*with id .*");
	private static Pattern messageTemplatePattern = Pattern.compile("\\{(.+)\\}");

	@Autowired
	private MessageSource messageSource;

	@Override
	@ResponseBody
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		logger.error("Rest API ERROR happen", ex);
		return super.handleTypeMismatch(ex, headers, status, request);
	}

	/**
	 * 表单提交数据校验错误，或者提交的数据转换成目标数据类型时候出错
	 * @on
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status,
			WebRequest request) {
		logger.error("Rest API ERROR happen", ex);
		headers.add("Content-Type", "application/json");
		ErrorMessage errorMessage = ValidationUtils.getErrorMessage(ex.getBindingResult());
		List<String> msgs = errorMessage.getErrors()
				.stream()
				.map(errArray -> errArray[1])
				.collect(toList());
		boolean isDebugMessage = false;
		for (String msg : msgs) {
			if(StringUtils.contains(msg, "Exception")) {
				isDebugMessage = true;
				break;
			}
		}
		
		Builder builder = Results.fail()
				.status(200)
				.code(BAD_REQUEST);
		if(!isDebugMessage) {
			builder.message(msgs);
		} else {
			builder.debugMessage(msgs);
		}
		return new ResponseEntity(builder.build(), headers, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error("Rest API ERROR happen", ex);
		headers.add("Content-Type", "application/json");
		Result result = Results.fail()
				.status(200)
				.code(BAD_REQUEST)
				.debugMessage(ex.getLocalizedMessage())
				.build();
		return new ResponseEntity(result, headers, HttpStatus.OK);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error("Rest API ERROR happen", ex);
		ErrorMessage errorMessage = ValidationUtils.getErrorMessage(ex.getBindingResult());
		List<String> msgs = errorMessage.getErrors()
				.stream()
				.map((errArray) -> {
					Matcher matcher = messageTemplatePattern.matcher(errArray[1]);
					if (matcher.matches()) {
						return MessageHelper.getMessage(matcher.group(1));
					}
					return errArray[1];
				})
				.collect(toList());
		Result result = Results.fail()
				.status(200)
				.code(BAD_REQUEST)
				.message(msgs)
				.build();
		return new ResponseEntity(result, headers, HttpStatus.OK);
	}

	/**
	 * 手工验证不通过时抛出
	 * @param e
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(GeneralValidationException.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	protected ResponseEntity<Object> handleMethodArgumentNotValid(GeneralValidationException e) {
		logger.error("Rest API ERROR happen", e);
		ErrorMessage errorMessage = e.getErrorMessage();
		List<String> msgs = errorMessage.getErrors()
				.stream()
				.map((errArray) -> {
					Matcher matcher = messageTemplatePattern.matcher(errArray[1]);
					if (matcher.matches()) {
						return MessageHelper.getMessage(matcher.group(1));
					}
					return errArray[1];
				})
				.collect(toList());
		Result result = Results.fail()
				.status(200)
				.code(BAD_REQUEST)
				.message(msgs)
				.build();
		return new ResponseEntity(result, HttpStatus.OK);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(com.loserico.commons.exception.EntityNotFoundException.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<Object> handleEntityNotFoundException(com.loserico.commons.exception.EntityNotFoundException e) {
		logger.error("Rest API ERROR happen", e);
		Matcher matcher = pattern.matcher(e.getMessage());
		String messageTemplate = null;
		String message = null;

		if (matcher.matches()) {
			String entity = matcher.group(1);
			messageTemplate = "EntityNotFound." + entity;
		} else {
			messageTemplate = e.getMessage();
		}

		try {
			message = MessageHelper.getMessage(messageTemplate);
		} catch (NoSuchMessageException e1) {
			logger.error(format("Cannot find message template {0}", messageTemplate), e1);
			message = MessageHelper.getMessage(ENTITY_NOT_FOUND.getMsgTemplate(),
					ENTITY_NOT_FOUND.getDefaultMsg());
		}
		Result result = Results.fail()
				.code(ENTITY_NOT_FOUND)
				.message(ofNullable(message).orElse("Entity not found"))
				.build();
		return new ResponseEntity(result, HttpStatus.OK);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(EntityNotFoundException.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException e) {
		logger.error("Rest API ERROR happen", e);
		Matcher matcher = pattern.matcher(e.getMessage());
		String messageTemplate = null;
		String message = null;
		
		if (matcher.matches()) {
			String entity = matcher.group(1);
			messageTemplate = "EntityNotFound." + entity;
		} else {
			messageTemplate = e.getMessage();
		}
		
		try {
			message = MessageHelper.getMessage(messageTemplate);
		} catch (NoSuchMessageException e1) {
			logger.error(format("Cannot find message template {0}", messageTemplate), e1);
			message = MessageHelper.getMessage(ENTITY_NOT_FOUND.getMsgTemplate(),
					ENTITY_NOT_FOUND.getDefaultMsg());
		}
		Result result = Results.fail()
				.code(ENTITY_NOT_FOUND)
				.message(message)
				.build();
		return new ResponseEntity(result, HttpStatus.OK);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(javax.persistence.EntityNotFoundException.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<Object> handleEntityNotFoundException(javax.persistence.EntityNotFoundException e) {
		logger.error("Rest API ERROR happen", e);
		Matcher matcher = pattern.matcher(e.getMessage());
		String messageTemplate = null;
		String message = null;
		
		if (matcher.matches()) {
			String entity = matcher.group(1);
			messageTemplate = "EntityNotFound." + entity;
		} else {
			messageTemplate = e.getMessage();
		}
		
		try {
			message = MessageHelper.getMessage(messageTemplate);
		} catch (NoSuchMessageException e1) {
			logger.error(format("Cannot find message template {0}", messageTemplate), e1);
			message = MessageHelper.getMessage(ENTITY_NOT_FOUND.getMsgTemplate(),
					ENTITY_NOT_FOUND.getDefaultMsg());
		}
		Result result = Results.fail()
				.code(ENTITY_NOT_FOUND)
				.message(message)
				.build();
		return new ResponseEntity(result, HttpStatus.OK);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(JpaObjectRetrievalFailureException.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public ResponseEntity<Object> handleJpaObjectRetrievalFailureException(JpaObjectRetrievalFailureException e) {
		logger.error("Rest API ERROR happen", e);
		Matcher matcher = objectRetrievalFailurePattern.matcher(e.getCause().getMessage());
		if (matcher.matches()) {
			String entity = matcher.group(1);
			String messageTemplate = "EntityNotFound." + entity;
			String message;
			try {
				message = messageSource.getMessage("EntityNotFound." + entity, null,
						LocaleContextHolder.getLocale());
			} catch (NoSuchMessageException e1) {
				logger.error(format("Cannot find message template {0}", messageTemplate), e1);
				message = MessageHelper.getMessage(ENTITY_NOT_FOUND.getMsgTemplate(),
						ENTITY_NOT_FOUND.getDefaultMsg());
			}
			Result result = Results.fail()
					.code(ENTITY_NOT_FOUND)
					.message(message)
					.results(asList(new String[] { entity, message }))
					.build();
			return new ResponseEntity(result, HttpStatus.OK);
		}
		return new ResponseEntity(e.getMessage(), HttpStatus.OK);
	}

	@ExceptionHandler(com.loserico.orm.exception.ApplicationException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Result handleApplicationException(com.loserico.orm.exception.ApplicationException e) {
		logger.error("Rest API ERROR happen", e);
		return Results.fail()
				.status(500)
				.code(StatusCode.INTERNAL_SERVER_ERROR)
				.build();
	}

	@ExceptionHandler(AbstractPropertyExistsException.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public @JsonRawValue ErrorMessage handlePropertyExistsException(AbstractPropertyExistsException e) {
		logger.error("Rest API ERROR happen", e);
		return ValidationUtils.getErrorMessage(e);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(CommonException.class)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<Object> handleCommonException(CommonException e) {
		logger.error("", e);
		Result result = Results.fail()
				.status(200)
				.code(e.getStatusCode())
				.message(e.getDefaultMessage())
				.build();
		return new ResponseEntity(result, HttpStatus.OK);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@ExceptionHandler(LocalizedException.class)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<Object> handleLocalizedException(LocalizedException e) {
		logger.error("", e);
		Result result = Results.fail()
				.status(200)
				.code(e.getStatusCode())
				.message(e.getLocalizedMessage())
				.build();
		return new ResponseEntity(result, HttpStatus.OK);
	}

	@ExceptionHandler(ApplicationException.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public Result handleApplicationException(ApplicationException e) {
		logger.error("Rest API ERROR happen", e);
		return Results.fail()
				.status(500)
				.code(StatusCode.INTERNAL_SERVER_ERROR)
				.build();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ExceptionHandler(Throwable.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ResponseEntity<Result> handleThrowable(Throwable e) {
		logger.error("Rest API ERROR happen", e);
		Result result = Results.fail()
				.status(200)
				.code(500)
				.message("Internal Server Error")
				.build();
		return new ResponseEntity(result, HttpStatus.OK);
	}
}