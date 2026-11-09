# V516 original Android Activity lifecycle QA-only gate

ZERO515 wild run [42701372977](https://github.com/nicogtr20-ctrl/idle-runner-releases/actions/runs/42701372977) FAILED before its first neural decision: original ActivityMonitor returned a GameActivity later marked destroyed, V356 owner false/cursor=-1, saved original brain had already been created (557,204 bytes). No measured motor result. On Android startup the original screen can be recreated while ActivityMonitor retains a stale object. This can be instrument-only false failure or a real app lifecycle defect; V516 must distinguish them.

V516 test listens to Application ActivityLifecycleCallbacks to track the latest created/resumed real GameActivity, accepts owner only on a live Activity, and at most once relaunches if the only observed instance is gone before owner establishment. It logs lifecycle transitions, does not reset/delete any persisted V356 brain or twelve named slots, does not change app source, physics, cortex, actor, thresholds, or original 10,000-decision red/blue strict acceptance.

No APK/OTA is authorized by a boot-only retry. Same-SHA independent 10,000 trained-S21 and wild safety must eventually pass; S21 V515 is running on its original parent branch without disruption.
