# Logic

## **Entity: Martian**
### Attributes:
- marsId (unique, varchar)
- name: firstname, lastname
- birthdate (date)
- profiles (Array: we only save the bought and configured profiles and get the "to be configured profiles" from a different api request)

### Relations:
- Profiles store the martianId of the Martian who owns that profile  

### Identifiers:
- marsId **unique, PK**
- profiles: [***profilenames***] **FK**

## **Entity: Profile**
### Attributes:
- name (chosen by the person or default value, unique)
- price
- traits (Set: dupplicates are allowed, we want every profile to have unique combinations)
- creation_date (datetime)
- used (true or false; defines if a profile is being used or not)
- counter (used to keep track of how many times the user switched to that profile)
  
### Relations:
- **name** is refering to the profiles the martians posses
### Identifiers:
- name **unique, PK**

## **Entity: Trait**
### Attributes:
- name (unique, PK)
- description
- type (Positive, Neutral, Negative)
- *profile*
- ...
### Relations:
- profile refers to the profile it is part of
### Identifiers:
- name **PK**
- profile **FK**