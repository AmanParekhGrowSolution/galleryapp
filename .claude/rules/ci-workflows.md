---
description: CI/CD guardrails — auto-loaded when GitHub Actions workflows or scripts are in context
paths:
  - ".github/**"
  - "scripts/**"
---

## CI/CD Rules

### Required secrets (must exist in repo Settings → Secrets)
| Secret                  | Used by                         | Purpose                              |
|-------------------------|---------------------------------|--------------------------------------|
| `CLAUDE_CODE_OAUTH_TOKEN` | all three Claude workflows    | Authenticates the Claude Code agent  |
| `AGENT_PAT`             | all three Claude workflows      | PAT so Claude's commits trigger CI   |
| `SLACK_WEBHOOK_URL`     | rollback.yml                    | APK delivery notifications           |

### Claude Code Action setup
- Action: `anthropics/claude-code-action@v1`
- Model: `claude-sonnet-4-6`
- Auth: `claude_code_oauth_token` (OAuth) — do NOT switch to API key
- Always pass `--permission-mode bypassPermissions` so the agent is not blocked mid-run
- Always pass `--debug-file ${{ runner.temp }}/claude-debug.json` and upload as an artifact

### Skills available to CI agents
Project-local skills at `.claude/skills/` are auto-available to any `claude-code-action` run:
- `android-admob` — invoke for AdMob / ad monetization issues
- `android-ui-replicator` — invoke when issue includes a UI screenshot

### Project MCP servers
Defined in `.mcp.json` at the repo root. Servers listed there are available to both
local sessions and CI agents (provided their runtime is installed on the runner).

### Off-limits files (never edit without explicit issue instruction)
- `.github/` workflows
- `gradle/`, `gradle.properties`, `settings.gradle*`
- Top-level `build.gradle*`
- `local.properties`

### Compliance script
```bash
bash scripts/check-claude-md.sh --diff origin/<base-branch>
```
Checks architecture (ARCH1/ARCH2), accessibility (A1), string resources (S2), security (SEC1–SEC10), and coroutine/performance/privacy advisories. Must exit 0 (no BLOCKING violations) before any PR is opened or pushed.
