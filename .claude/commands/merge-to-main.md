Merge the current branch into main and clean up.

1. Get the current branch name
2. git checkout main && git pull
3. git merge <branch> --no-ff
4. git push origin main
5. git branch -d <branch>
6. git push origin --delete <branch>