package edu.brown.hackathon.fifteenminutes;

import com.google.appengine.api.datastore.Entity;

/**
 *  All the info about a famous user that is sent to the client.
 */
public class UserResource {
 
  private long userId;
  private long oldUserId;

  public UserResource(long userId, long oldUserId) {
    this.userId = userId;
    this.oldUserId = oldUserId;
  }
  
  public long getUserId() {
    return userId;
  }
  
  public static long parseUserIdFromEntity(Entity user) {
    Object userId = user.getProperty("user_id");
    if (userId instanceof Long) {
      return (Long) userId;
    } else {
      return Long.parseLong((String) userId);
    }
  }
}