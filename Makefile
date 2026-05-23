# ==============================================================================
# Variables
# ==============================================================================
.DEFAULT_GOAL := help

ifeq ($(OS),Windows_NT)
    GRADLE_CMD = gradlew.bat
    RM_RF = if exist $(subst /,\,$(1)) rd /s /q $(subst /,\,$(1))
else
    GRADLE_CMD = ./gradlew
    RM_RF = rm -rf $(1)
endif

.PHONY: help
help:
		@echo "Available commands:"
		@echo "  build        Build all projects"
		@echo "  clean        Clean projects and remove temporary folders including run directories"
		@echo "  audio        Fix and convert audio files (Linux/macOS only)"
		@echo "  create-test  Create a test version (Usage: make create-test [vampirism|faction|all] <test_id>)"
		@echo "  help         Show this help message"

.PHONY: build
build:
		@echo "==> Building projects..."
		$(GRADLE_CMD) build

.PHONY: clean
clean:
		@echo "==> Running Gradle clean..."
		$(GRADLE_CMD) clean
		@echo "==> Removing project run folders..."
		$(call RM_RF,build)
		$(call RM_RF,projects/faction/out)
		$(call RM_RF,projects/faction/run)
		$(call RM_RF,projects/faction-api/out)
		$(call RM_RF,projects/faction-api/run)
		$(call RM_RF,projects/vampirism/out)
		$(call RM_RF,projects/vampirism/run)
		$(call RM_RF,projects/vampirism-api/out)
		$(call RM_RF,projects/vampirism-api/run)
.PHONY: audio
audio:
ifeq ($(OS),Windows_NT)
		$(error Audio conversion is not supported on Windows)
endif
		@echo "==> Fixing Audio files..."
		sh ./scripts/convert-audio.sh

# Catch-all target to allow passing arguments to create-test
ifeq ($(firstword $(MAKECMDGOALS)),create-test)
  ifneq ($(words $(MAKECMDGOALS)),1)
    $(eval $(wordlist 2,$(words $(MAKECMDGOALS)),$(MAKECMDGOALS)):;@:)
  endif
endif

.PHONY: create-test
create-test:
		@$(if $(word 3,$(MAKECMDGOALS)),,$(error Missing test id. Usage: make create-test [vampirism|faction|all] <test_id>))
		@$(if $(filter vampirism,$(word 2,$(MAKECMDGOALS))),$(GRADLE_CMD) vampirism:build -Pvampirism_test_id=$(word 3,$(MAKECMDGOALS)), \
			$(if $(filter faction,$(word 2,$(MAKECMDGOALS))),$(GRADLE_CMD) faction:build -Pfaction_test_id=$(word 3,$(MAKECMDGOALS)), \
			$(if $(filter all,$(word 2,$(MAKECMDGOALS))),$(GRADLE_CMD) faction:build vampirism:build -Pfactions_test_id=$(word 3,$(MAKECMDGOALS)) -Pvampirism_test_id=$(word 3,$(MAKECMDGOALS)) -Pvampirism_factions_source_dependency=true, \
			$(error Unknown project: $(word 2,$(MAKECMDGOALS)). Usage: make create-test [vampirism|faction|all] <test_id>))))