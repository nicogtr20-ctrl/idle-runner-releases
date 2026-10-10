# OVERMIND ZERO — voluntary GPT data feed

This is the data-only branch for user-approved exports from Android **ZERO 0.11.6+**.

Read `zero-telemetry/current.json` **from this branch** (`ref=zero-telemetry`) to analyze the current character and the last behavioral A/B report. This file does not exist until the user presses **CONDIVIDI DATI CON GPT** on the phone with their own GitHub fine-grained personal access token, scoped to the sole repository `nicogtr20-ctrl/idle-runner-releases` with Contents read/write.

Immutable chronological snapshots appear at `zero-telemetry/history/*.json`. The app commits one historical entry first and then updates `current.json`, with any prior `sha` fetched from GitHub. Uploads are **never scheduled or automatic**, and no telemetry server or GitHub credentials are stored in the code, the report or the URL.

The report includes scalar preferences, serialized model weights, trained control/memory parameters, full life statistics (as stored in the current working profile) and the last completed behavioral laboratory. Excludes the other 12 named save slots and user/device identifiers. User's upload acts on copies read from `zero_motorbrain_001`, not on the saved data.

In ChatGPT: use the connected GitHub tool to fetch `zero-telemetry/current.json` with `repository_full_name="nicogtr20-ctrl/idle-runner-releases"`, `ref="zero-telemetry"`. The repository is **private**, so a public web browser without access cannot read it. The user need only say “ho condiviso i dati di ZERO” after seeing success on the phone.

**DO NOT mistake diagnostic counters for proof of intentional or causal cognition.** Prefer changes in lifetime and out-of-sample error, comparisons by actual outcome, and snapshots from the same saved character. Never write to game save files when analyzing.
