package com.loserico.junit.spring;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestExecutionListeners.MergeMode;

/**
 * To avoid the redeclaration of all default listeners, the mergeMode attribute
 * of @TestExecutionListeners can be set to MergeMode.MERGE_WITH_DEFAULTS. The
 * MERGE_WITH_DEFAULTS part indicates that locally declared listeners should be
 * merged with the default listeners, as shown in the following listing
 * 
 * @author Loser
 * @since Aug 8, 2016
 * @version
 *
 */
@ContextConfiguration
@TestExecutionListeners(listeners = ExecutionListenerSysOutTest.class, mergeMode = MergeMode.MERGE_WITH_DEFAULTS)
public class ExecutionListenerTest3 {
}