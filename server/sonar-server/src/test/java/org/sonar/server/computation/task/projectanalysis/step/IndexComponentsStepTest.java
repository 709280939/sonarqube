/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
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
package org.sonar.server.computation.task.projectanalysis.step;

import org.junit.Rule;
import org.junit.Test;
import org.sonar.db.component.ResourceIndexDao;
import org.sonar.server.component.index.ComponentIndexer;
import org.sonar.server.computation.task.projectanalysis.batch.BatchReportReaderRule;
import org.sonar.server.computation.task.projectanalysis.component.Component;
import org.sonar.server.computation.task.projectanalysis.component.ReportComponent;
import org.sonar.server.computation.task.projectanalysis.component.TreeRootHolderRule;
import org.sonar.server.computation.task.projectanalysis.component.ViewsComponent;
import org.sonar.server.computation.task.step.ComputationStep;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.sonar.server.computation.task.projectanalysis.component.Component.Type.PROJECT;
import static org.sonar.server.computation.task.projectanalysis.component.Component.Type.VIEW;

public class IndexComponentsStepTest extends BaseStepTest {

  private static final String PROJECT_KEY = "PROJECT_KEY";
  private static final String PROJECT_UUID = "PROJECT_UUID";

  @Rule
  public TreeRootHolderRule treeRootHolder = new TreeRootHolderRule();
  @Rule
  public BatchReportReaderRule reportReader = new BatchReportReaderRule();

  ResourceIndexDao resourceIndexDao = mock(ResourceIndexDao.class);
  ComponentIndexer elasticSearchIndexer = mock(ComponentIndexer.class);
  IndexComponentsStep underTest = new IndexComponentsStep(resourceIndexDao, elasticSearchIndexer, treeRootHolder);

  @Test
  public void call_indexProject_of_dao_for_project() {
    Component project = ReportComponent.builder(PROJECT, 1).setUuid(PROJECT_UUID).setKey(PROJECT_KEY).build();
    treeRootHolder.setRoot(project);

    underTest.execute();

    verify(resourceIndexDao).indexProject(PROJECT_UUID);
  }

  @Test
  public void call_indexProject_of_dao_for_view() {
    Component view = ViewsComponent.builder(VIEW, PROJECT_KEY).setUuid(PROJECT_UUID).build();
    treeRootHolder.setRoot(view);

    underTest.execute();

    verify(resourceIndexDao).indexProject(PROJECT_UUID);
  }

  @Override
  protected ComputationStep step() {
    return underTest;
  }
}