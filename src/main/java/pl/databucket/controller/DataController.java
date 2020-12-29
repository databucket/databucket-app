package pl.databucket.controller;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.databucket.database.Condition;
import pl.databucket.database.FieldValidator;
import pl.databucket.database.ResultField;
import pl.databucket.exception.*;
import pl.databucket.service.DataService;
import pl.databucket.response.BaseResponse;
import pl.databucket.response.DataResponse;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("/api")
@RestController
public class DataController {

  private final CustomExceptionFormatter customExceptionFormatter;
  private final DataService service;

  public DataController(DataService service) {
    this.service = service;
    this.customExceptionFormatter = new CustomExceptionFormatter(LoggerFactory.getLogger(DataController.class));
  }

  @PostMapping(value =
          "/bucket/{bucketName}/data/custom",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse> getDataCustom(
      @PathVariable String bucketName,
      @RequestParam(required = false) Optional<Integer> page,
      @RequestParam(required = false) Optional<Integer> limit,
      @RequestParam(required = false) Optional<String> sort,
      @RequestBody LinkedHashMap<String, Object> body) {

    DataResponse response = new DataResponse();

    try {
      if (page.isPresent()) {
        FieldValidator.mustBeGraterThen0("page", page.get());
        response.setPage(page.get());
      }

      if (limit.isPresent()) {
        FieldValidator.mustBeGraterOrEqual0("limit", limit.get());
        response.setLimit(limit.get());
      }

      if (sort.isPresent()) {
        FieldValidator.validateSort(sort.get());
        response.setSort(sort.get());
      }

      List<Map<String, Object>> columns = FieldValidator.validateColumns(body, false);
      List<Condition> conditions = FieldValidator.validateListOfConditions(body, false);

      Map<ResultField, Object> result = service.getData(bucketName, Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.ofNullable(columns), Optional.ofNullable(conditions), page, limit, sort);

      long total = (long) result.get(ResultField.TOTAL);
      response.setTotal(total);

      if (page.isPresent() && limit.isPresent()) {
        response.setTotalPages((int) Math.ceil(total / (float) limit.get()));
      }

      response.setData((List<Map<String, Object>>) result.get(ResultField.DATA));

      if (response.getData().size() <= 0) {
        if (limit.get() > 0) {
          response.setMessage("No data meets the given requirements!");
        }
      }
      return new ResponseEntity<>(response, HttpStatus.OK);

    } catch (ItemDoNotExistsException e1) {
      return customExceptionFormatter.customException(response, e1, HttpStatus.NOT_FOUND);
    } catch (IncorrectValueException | UnknownColumnException | ConditionNotAllowedException e2) {
      return customExceptionFormatter.customException(response, e2, HttpStatus.NOT_ACCEPTABLE);
    } catch (Exception ee) {
      return customExceptionFormatter.defaultException(response, ee);
    }
  }
  
  @GetMapping(value = {
      "/bucket/{bucketName}/data",
      "/bucket/{bucketName}/data/{dataId}",
      "/bucket/{bucketName}/data/tags/{tagId}",
      "/bucket/{bucketName}/data/filters/{filterId}",
      "/bucket/{bucketName}/data/views/{viewId}"},
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse> getData(
      @PathVariable String bucketName,
      @PathVariable(required = false) Optional<Integer[]> dataId,
      @PathVariable(required = false) Optional<Integer[]> tagId,
      @PathVariable(required = false) Optional<Integer> filterId,
      @PathVariable(required = false) Optional<Integer> viewId,
      @RequestParam(required = false) Optional<Integer> page,
      @RequestParam(required = false) Optional<Integer> limit,
      @RequestParam(required = false) Optional<String> sort) {

    DataResponse response = new DataResponse();
    try {
      if (page.isPresent()) {
        FieldValidator.mustBeGraterThen0("page", page.get());
        response.setPage(page.get());
      }

      if (limit.isPresent()) {
        FieldValidator.mustBeGraterOrEqual0("limit", limit.get());
        response.setLimit(limit.get());
      }

      if (sort.isPresent()) {
        FieldValidator.validateSort(sort.get());
        response.setSort(sort.get());
      }

      Map<ResultField, Object> result = service.getData(bucketName, dataId, tagId, filterId, viewId, Optional.empty(), Optional.empty(), page, limit, sort);

      long total = (long) result.get(ResultField.TOTAL);
      response.setTotal(total);

      if (page.isPresent() && limit.isPresent()) {
        response.setTotalPages((int) Math.ceil(total / (float) limit.get()));
      }

      response.setData((List<Map<String, Object>>) result.get(ResultField.DATA));

      if (response.getData().size() <= 0) {
        response.setMessage("No data meets the given requirements!");
      }
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (ItemDoNotExistsException e1) {
      return customExceptionFormatter.customException(response, e1, HttpStatus.NOT_FOUND);
    } catch (IncorrectValueException | UnknownColumnException | ConditionNotAllowedException e2) {
      return customExceptionFormatter.customException(response, e2, HttpStatus.NOT_ACCEPTABLE);
    } catch (Exception ee) {
      return customExceptionFormatter.defaultException(response, ee);
    }
  }

  @GetMapping(value = {
      "/bucket/{bucketName}/data/lock",
      "/bucket/{bucketName}/data/tags/{tagId}/lock",
      "/bucket/{bucketName}/data/filters/{filterId}/lock"},
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse> lockData(
      @PathVariable String bucketName,
      @PathVariable(required = false) Optional<Integer[]> tagId,
      @PathVariable(required = false) Optional<Integer> filterId,
      @RequestParam String userName,
      @RequestParam(required = false) Optional<Integer> page,
      @RequestParam(required = false) Optional<Integer> limit,
      @RequestParam(required = false) Optional<String> sort) {

    DataResponse response = new DataResponse();

    try {
      if (page.isPresent()) {
        FieldValidator.mustBeGraterThen0("page", page.get());
        response.setPage(page.get());
      }

      if (limit.isPresent()) {
        FieldValidator.mustBeGraterOrEqual0("limit", limit.get());
        response.setLimit(limit.get());
      }

      if (sort.isPresent()) {
        FieldValidator.validateSort(sort.get());
        response.setSort(sort.get());
      }

      List<Integer> dataIdsList = service.lockData(bucketName, userName, tagId, filterId, Optional.empty(), page, limit, sort);

      if (dataIdsList != null && dataIdsList.size() > 0) {
        Integer[] dataIds = dataIdsList.toArray(new Integer[dataIdsList.size()]);
        return getData(bucketName, Optional.of(dataIds), Optional.empty(), Optional.empty(), Optional.empty(), page, limit, sort);
      } else {
        response.setMessage("No data meets the given requirements!");
        response.setDataIds(null);
        return new ResponseEntity<>(response, HttpStatus.OK);
      }
    } catch (ItemDoNotExistsException e) {
      return customExceptionFormatter.customException(response, e, HttpStatus.NOT_FOUND);
    } catch (IncorrectValueException | UnknownColumnException | ConditionNotAllowedException e2) {
      return customExceptionFormatter.customException(response, e2, HttpStatus.NOT_ACCEPTABLE);
    } catch (Exception ee) {
      return customExceptionFormatter.defaultException(response, ee);
    }
  }

  // Lock data with given conditions
  @PostMapping(value = {
          "/bucket/{bucketName}/data/custom/lock"},
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse> lockData(
      @PathVariable("bucketName") String bucketName,
      @RequestParam String userName,
      @RequestParam(required = false) Optional<Integer> page,
      @RequestParam(required = false) Optional<Integer> limit,
      @RequestParam(required = false) Optional<String> sort,
      @RequestBody LinkedHashMap<String, Object> body) {

    DataResponse response = new DataResponse();

    try {
      if (page.isPresent()) {
        FieldValidator.mustBeGraterThen0("page", page.get());
        response.setPage(page.get());
      }

      if (limit.isPresent()) {
        FieldValidator.mustBeGraterOrEqual0("limit", limit.get());
        response.setLimit(limit.get());
      }

      if (sort.isPresent()) {
        FieldValidator.validateSort(sort.get());
        response.setSort(sort.get());
      }

      List<Condition> conditions = FieldValidator.validateListOfConditions(body, true);

      List<Integer> dataIdsList = service.lockData(bucketName, userName, Optional.empty(), Optional.empty(), Optional.of(conditions), page, limit, sort);
      if (dataIdsList != null && dataIdsList.size() > 0) {
        Integer[] dataIds = dataIdsList.toArray(new Integer[dataIdsList.size()]);
        return getData(bucketName, Optional.of(dataIds), Optional.empty(), Optional.empty(), Optional.empty(), page, limit, sort);
      } else {
        response.setMessage("No data meets the given requirements!");
        response.setDataIds(null);
        return new ResponseEntity<>(response, HttpStatus.OK);
      }
    } catch (ItemDoNotExistsException e) {
      return customExceptionFormatter.customException(response, e, HttpStatus.NOT_FOUND);
    } catch (IncorrectValueException | UnknownColumnException | ConditionNotAllowedException e2) {
      return customExceptionFormatter.customException(response, e2, HttpStatus.NOT_ACCEPTABLE);
    } catch (Exception ee) {
      return customExceptionFormatter.defaultException(response, ee);
    }
  }

  @PostMapping(value =
          "/bucket/{bucketName}/data",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse> createData(
      @PathVariable("bucketName") String bucketName,
      @RequestParam String userName,
      @RequestBody Map<String, Object> body) {

    DataResponse response = new DataResponse();
    try {
      Integer dataId = service.createData(userName, bucketName, body);
      response.setDataId(dataId);
      response.setMessage("The new data has been successfully created.");
      return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (ItemDoNotExistsException e1) {
      return customExceptionFormatter.customException(response, e1, HttpStatus.NOT_FOUND);
    } catch (UnknownColumnException | ConditionNotAllowedException e2) {
      return customExceptionFormatter.customException(response, e2, HttpStatus.NOT_ACCEPTABLE);
    } catch (Exception ee) {
      return customExceptionFormatter.defaultException(response, ee);
    }
  }

  @PutMapping(value = {
      "/bucket/{bucketName}/data",
      "/bucket/{bucketName}/data/{dataIds}",
      "/bucket/{bucketName}/data/filters/{filterId}",
      "/bucket/{bucketName}/data/tags/{tagsIds}"}, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse> modifyData(
      @PathVariable String bucketName,
      @PathVariable Optional<Integer[]> dataIds,
      @PathVariable Optional<Integer> filterId,
      @PathVariable Optional<Integer[]> tagsIds,
      @RequestParam() String userName,
      @RequestBody() LinkedHashMap<String, Object> body) {

    DataResponse response = new DataResponse();

    try {
      int count = service.modifyData(userName, bucketName, dataIds, filterId, tagsIds, body);
      response.setMessage("Number of modified data: " + count);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (ItemDoNotExistsException e1) {
      return customExceptionFormatter.customException(response, e1, HttpStatus.NOT_FOUND);
    } catch (UnknownColumnException | ConditionNotAllowedException e2) {
      return customExceptionFormatter.customException(response, e2, HttpStatus.NOT_ACCEPTABLE);
    } catch (Exception ee) {
      return customExceptionFormatter.defaultException(response, ee);
    }
  }

  @PutMapping(value =
          "/bucket/{bucketName}/data/custom",
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse> modifyDataCustom(
          @PathVariable String bucketName,
          @RequestParam String userName,
          @RequestBody LinkedHashMap<String, Object> body) {

    DataResponse response = new DataResponse();

    try {
      int count = service.modifyData(userName, bucketName, Optional.empty(), Optional.empty(), Optional.empty(), body);
      response.setMessage("Number of modified data: " + count);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (ItemDoNotExistsException e1) {
      return customExceptionFormatter.customException(response, e1, HttpStatus.NOT_FOUND);
    } catch (UnknownColumnException | ConditionNotAllowedException e2) {
      return customExceptionFormatter.customException(response, e2, HttpStatus.NOT_ACCEPTABLE);
    } catch (Exception ee) {
      return customExceptionFormatter.defaultException(response, ee);
    }
  }

  @DeleteMapping(value = {
      "/bucket/{bucketName}/data",
      "/bucket/{bucketName}/data/{dataIds}",
      "/bucket/{bucketName}/data/filters/{filterId}",
      "/bucket/{bucketName}/data/tags/{tagsIds}"}, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse> deleteData(
      @PathVariable String bucketName,
      @PathVariable Optional<Integer[]> dataIds,
      @PathVariable Optional<Integer> filterId,
      @PathVariable Optional<Integer[]> tagsIds) {

    DataResponse response = new DataResponse();

    try {
      int count = service.deleteData(bucketName, dataIds, filterId, tagsIds);
      response.setMessage("Number of removed data: " + count);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (ItemDoNotExistsException e) {
      return customExceptionFormatter.customException(response, e, HttpStatus.NOT_FOUND);
    } catch (UnknownColumnException | ConditionNotAllowedException e2) {
      return customExceptionFormatter.customException(response, e2, HttpStatus.NOT_ACCEPTABLE);
    } catch (Exception ee) {
      return customExceptionFormatter.defaultException(response, ee);
    }
  }

  @DeleteMapping(value = {
          "/bucket/{bucketName}/data/custom"},
          produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BaseResponse> deleteDataCustom(
          @PathVariable String bucketName,
          @RequestBody LinkedHashMap<String, Object> body) {

    DataResponse response = new DataResponse();

    try {
      List<Condition> conditions = FieldValidator.validateListOfConditions(body, false);
      int count = service.deleteData(bucketName, conditions);
      response.setMessage("Number of removed data: " + count);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (ItemDoNotExistsException e) {
      return customExceptionFormatter.customException(response, e, HttpStatus.NOT_FOUND);
    } catch (UnknownColumnException | ConditionNotAllowedException e2) {
      return customExceptionFormatter.customException(response, e2, HttpStatus.NOT_ACCEPTABLE);
    } catch (Exception ee) {
      return customExceptionFormatter.defaultException(response, ee);
    }
  }
}