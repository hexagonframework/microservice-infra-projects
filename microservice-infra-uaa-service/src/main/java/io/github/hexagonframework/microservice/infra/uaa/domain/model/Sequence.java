package io.github.hexagonframework.microservice.infra.uaa.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Xuegui Yuan
 */
@Document(collection = "sequences")
public class Sequence {
  @Id
  private String id;

  private long seq;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public long getSeq() {
    return seq;
  }

  public void setSeq(long seq) {
    this.seq = seq;
  }
}
