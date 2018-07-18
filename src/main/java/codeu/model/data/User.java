// Copyright 2017 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package codeu.model.data;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** Class representing a registered user. */
public class User implements Activity{
  private final UUID id;
  private final String name;
  private final String passwordHash;
  private final Instant creation;
  private String aboutMe;
  //private List<Conversation> conversations;
  private List<UUID> conversations;

  /**
   * Constructs a new User.
   *
   * @param id the ID of this User
   * @param name the username of this User
   * @param passwordHash the password hash of this User
   * @param creation the creation time of this User
   */
  public User(UUID id, String name, String passwordHash, Instant creation, String aboutMe) {
    this.id = id;
    this.name = name;
    this.passwordHash = passwordHash;
    this.creation = creation;
    this.aboutMe = aboutMe;
    this.conversations = new ArrayList<UUID>();
  }

  /** Returns the ID of this User. */
  public UUID getId() {
    return id;
  }

  /** Returns the username of this User. */
  public String getName() {
    return name;
  }

  /** Returns the password hash of this User. */
  public String getPasswordHash() {
    return passwordHash;
  }

  /** Returns the creation time of this User. */
  public Instant getCreationTime() {
    return creation;
  }
  
  /** Returns the list of all conversations this user is a member of. */
  public List<UUID> getConversations() {
	  return conversations;
  }
  
  public void setConversations(List<UUID> conversations) {
	  this.conversations = conversations;
  }
  
  /** Adds the conversation to the list of conversations the user is a member of. */
  public void addConversation(UUID conversation) {
	  conversations.add(conversation);
  }

  @Override
  public String getDisplayText() {
	return name + " joined!";
  }
  
  @Override
  public int compareTo(Activity a) {
	  return creation.compareTo(a.getCreationTime());
  }
  
  public void setAboutMe(String aboutMe) {
    this.aboutMe = aboutMe;
  }

  public String getAboutMe() {
    return aboutMe;
  }
}
