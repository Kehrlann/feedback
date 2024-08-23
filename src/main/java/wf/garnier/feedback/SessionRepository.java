package wf.garnier.feedback;

import java.util.List;
import java.util.Optional;

import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;

interface SessionRepository extends DatastoreRepository<Session, String> {

	List<Session> findSessionByActiveEquals(Boolean active);

	void deleteBySessionId(String sessionId);

	Optional<Session> findSessionBySessionId(String sessionId);

	boolean existsBySessionId(String sessionId);

}
