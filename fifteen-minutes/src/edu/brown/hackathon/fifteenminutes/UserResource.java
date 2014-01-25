package edu.brown.hackathon.fifteenminutes;

/**
 *  All the info about a famous user
 *  that is sent to the client.
 */
public class UserResource {
 
  private int userId;
  private String profilePicture;

  public UserResource(int userId, String profilePicture) {
    this.userId = userId;
    this.profilePicture = profilePicture;
  }
}