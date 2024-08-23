package wf.garnier.feedback;

import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;

public interface SessionVoteRepository extends DatastoreRepository<SessionVote, String> {

}
