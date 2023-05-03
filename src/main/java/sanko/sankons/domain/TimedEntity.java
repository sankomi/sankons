package sanko.sankons.domain;

import java.time.LocalDateTime;
import jakarta.persistence.*; //EntityListeners, MappedSuperclass

import org.springframework.data.annotation.*; //CreatedDate, LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class TimedEntity {

	@CreatedDate
	private LocalDateTime created;

	@LastModifiedDate
	private LocalDateTime modified;

}
