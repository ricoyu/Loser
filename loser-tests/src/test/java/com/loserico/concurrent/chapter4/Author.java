package com.loserico.concurrent.chapter4;

public class Author {

  private final String name;

  public String getName() {
    return name;
  }

  public Author(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "Author [name=" + name + "]";
  }

}
