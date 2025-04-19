# Contributing to Charts

Thank you for your time!

# Contributions 
New issue: https://github.com/dautovicharis/Charts/issues
Feel free to create issue for any suggestions, improvement ideas, feature requests, or bugs. 

## Git setup
#### Fork repo
 - Fork repository: https://github.com/dautovicharis/charts
 - Clone forked repository locally
 - Add upstream
   ```bash
   git remote add upstream https://github.com/dautovicharis/Charts.git
   ```

#### Sync **origin/main** with **upstream/main**
```bash
 git fetch upstream
 git checkout main
 git merge upstream/main
 git push origin main
```

## Branch naming convention
```bash
bugfix/<bug-name>
feature/<feature-name>
improvement/<improvement-name>
update/<update-name>
```

Example:
```bash
bugfix/bar-chart-animation
bugfix/line-chart-shadow

feature/line-chart
feature/styles
feature/documentation

update/docs
update/dependencies

improvement/styles
improvement/app-demo
```

## Commit
 - Write in imperative mode:  Start the line with 'Fix', 'Add', 'Update' instead of 'Fixed', 'Added', 'Updated'
 - Capitalize the subject line

## New branches
Create new branch from **main**

## PR
Create new PR to **upstream/main**
