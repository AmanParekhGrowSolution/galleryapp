---
name: continuous-learning
description: Pattern extraction and skill generation for mobile development sessions. Automatically learns from your coding patterns.
trigger:
  keywords: ["extract pattern", "generate skill", "learn from session", "continuous learning", "create skill from"]
  when: User wants to extract reusable patterns from the current session into a new project skill file
---

# Continuous Learning for Mobile

Extract patterns from your mobile development sessions to create reusable skills.

## How It Works

1. **Session Hooks** - Post-session analysis extracts patterns
2. **Pattern Storage** - Patterns saved to `.claude/instincts/`
3. **Skill Evolution** - Related patterns cluster into skills

## Pattern Types

- **Compose patterns** - UI component structures, state management
- **Architecture decisions** - Module organization, layer patterns
- **Error handling** - API error mapping, fallback strategies
- **Testing patterns** - Test structure, mocking approaches
- **Build patterns** - Dependency management, configuration

## Commands

```bash
/learn                  # Extract patterns now
/instinct-status        # View learned patterns
/instinct-export        # Export for sharing
/evolve                 # Cluster into skills
```

## Integration

Runs automatically via hooks:
- `PreCompact` - Saves session context
- `Stop` - Extracts patterns from session

---

**Remember**: The more you code, the smarter your agent becomes.
