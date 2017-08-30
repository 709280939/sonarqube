/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package ce;

import java.io.File;
import org.sonar.api.ce.posttask.PostProjectAnalysisTask;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

public class CePauseStep implements PostProjectAnalysisTask {

  private static final Logger LOGGER = Loggers.get(CePauseStep.class);

  private final Configuration configuration;

  public CePauseStep(Configuration configuration) {
    this.configuration = configuration;
  }

  @Override
  public void finished(ProjectAnalysis analysis) {
    configuration.get("sonar.ce.pauseTask.path").ifPresent(this::waitForFileToBeDeleted);
  }

  private void waitForFileToBeDeleted(String path) {
    LOGGER.info("CE analysis is paused. Waiting for file to be deleted: " + path);
    File file = new File(path);
    try {
      while (file.exists()) {
        Thread.sleep(500L);
      }
      LOGGER.info("CE analysis is resumed");
    } catch (InterruptedException e) {
      LOGGER.info("CE analysis has been interrupted");
      Thread.currentThread().interrupt();
    }
  }
}
