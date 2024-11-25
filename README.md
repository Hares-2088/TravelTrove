# TravelTrove
### Branch Naming

- Branches will be named according to the following convention: type/JiraID_Description
  I like to break it down into 4 'folders' or types:
    - feat/
    - bug/
    - doc/
    - conf/
- Start with the JIRA id (it will be something like TT-44).
- The full branch name would look like this `feat/TT-4_View_Packages` and would be created and navigated to by executing the following command:

```
git checkout -b feat/TT-4_Add_Test_Scenario_New_Pet
```

### Pull Requests (PR) Naming

- To make it so we can easily search and find pull requests we will adhere to the following standard:

```
feat(JiraID): short description
```

- In that example, you would replace the JIRA-TICKET-ID with the id from Jira.
- Keep the parentheses.
- Do not include any capital letters or punctuation in the description

### Pull Request Commit Naming

- This is pretty much the exact same as the Pull Request Naming except that at the end there will be an auto-generated number in parentheses. Please don't delete it. Simply add your stuff before it.

```
feat(JiraID): short description (#420)
```

