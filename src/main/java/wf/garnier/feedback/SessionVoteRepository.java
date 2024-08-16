package wf.garnier.feedback;

import java.util.List;

import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;

public interface SessionVoteRepository extends DatastoreRepository<SessionVote, String> {

	List<SessionVote> findAllBySessionId(String sessionId);

}
