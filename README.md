# TEST Change commit message

Here is how to change committed messages.

-   Use `git commit --amend` to change the latest commit message.
-   For the olds commit message:
    -   Step 1: `git rebase -i HEAD~n` with n the number of commit you want to change start from the latest.
    -   Step 2: `Select commits you want to change and edit "pick" with "reword"`. Then, save by typing `:x`
    -
