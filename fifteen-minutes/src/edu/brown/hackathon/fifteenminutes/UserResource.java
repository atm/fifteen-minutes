package edu.brown.hackathon.fifteenminutes;

import com.google.appengine.api.datastore.Entity;

/**
 *  All the info about a famous user that is sent to the client.
 */
public class UserResource {
 
  private long userId;

  public UserResource(long userId) {
    this.userId = userId;
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