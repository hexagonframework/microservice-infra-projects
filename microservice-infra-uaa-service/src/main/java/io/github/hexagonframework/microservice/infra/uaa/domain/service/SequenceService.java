package io.github.hexagonframework.microservice.infra.uaa.domain.service;

import io.github.hexagonframework.microservice.infra.uaa.domain.exception.SequenceException;
import io.github.hexagonframework.microservice.infra.uaa.domain.model.Sequence;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * @author Xuegui Yuan
 */
public class SequenceService {
  private MongoOperations mongoOperation;

  public SequenceService(MongoOperations mongoOperation) {
    this.mongoOperation = mongoOperation;
  }

  public long getNextSequenceId(String key) throws SequenceException {

    //get sequence id
    Query query = new Query(Criteria.where("_id").is(key));

    //increase sequence id by 1
    Update update = new Update();
    update.inc("seq", 1);

    //return new increased id
    FindAndModifyOptions options = new FindAndModifyOptions();
    options.returnNew(true);

    //this is the magic happened.
    Sequence seqId =
        mongoOperation.findAndModify(query, update, options, Sequence.class);

    //if no id, throws SequenceException
    //optional, just a way to tell user when the sequence id is failed to generate.
    if (seqId == null) {
      throw new SequenceException("Unable to get sequence id for key : " + key);
    }

    return seqId.getSeq();
  }

}
