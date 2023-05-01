```
Table martian {
  marsId int [pk]
  firstname varchar(50)
  lastname varchar(50)
  birthdate date
  cooldown_start_moment datetime
}

Table profile {
  name varchar(50) [pk]
  marsId int [ref: > martian.marsId]
  price varchar(50)
  creation_date date
  counter int
  used bit
  locked bit
}

Table traits {
  name varchar(50) [pk]
  desciption varchar(512)
  type traitType
}

Table martian_traits {
  name varchar(50) [ref: - traits.name]
  marsId int [ref: - martian.marsId]
}

Table profile_trait {
  profileName varchar(50) [ref: <> profile.name]
  traitName varchar(50) [ref: - traits.name]
}

Table chip {
  id int [ref:- martian.marsId]
  activated boolean
}

enum traitType {
  POSITIVE
  NEUTRAL
  NEGATIVE
}

Table notification {
  id int
  title string
  options string
}
```