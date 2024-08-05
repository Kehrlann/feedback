package wf.garnier.feedback;

import java.util.List;

import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;

interface SessionRepository extends DatastoreRepository<Session, String> {

	List<Session> findSessionByActiveEquals(Boolean active);

	void deleteBySessionId(String sessionId);

}
