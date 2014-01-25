package edu.brown.hackathon.fifteenminutes;

/**
 *  All the info about a famous user
 *  that is sent to the client.
 */
public class UserResource {
  
  private String username;
  private String profilePicture;

  public UserResource(String username, String profilePicture) {
    this.username = username;
    this.profilePicture = profilePicture;
  }
}