Review the pull request at $ARGUMENTS.

You have full context of this project from CLAUDE.md. Analyse the diff against these specific criteria:

1. **Architecture violations** — does anything break the layer dependency rules?
2. **Kotlin/Compose conventions** — LiveData, RxJava, or non-StateFlow patterns introduced?
3. **Missing error states** — does UiState handle error cases properly?
4. **Coroutine scope leaks** — any `GlobalScope` usage or missing cancellation?
5. **New dependencies** — anything added outside of libs.versions.toml?
6. **Test coverage** — are new use cases or ViewModels tested?

Flag issues as HIGH / MEDIUM / LOW. Skip LOW if there are more than 5 issues total.