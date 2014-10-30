/*
 * Copyright 2014 Open Networking Laboratory
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onlab.onos.net.intent.impl;

import com.google.common.collect.Lists;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.ReferenceCardinality;
import org.onlab.onos.core.ApplicationId;
import org.onlab.onos.core.CoreService;
import org.onlab.onos.net.ConnectPoint;
import org.onlab.onos.net.Link;
import org.onlab.onos.net.flow.DefaultFlowRule;
import org.onlab.onos.net.flow.DefaultTrafficSelector;
import org.onlab.onos.net.flow.DefaultTrafficTreatment;
import org.onlab.onos.net.flow.FlowRule;
import org.onlab.onos.net.flow.FlowRuleBatchEntry;
import org.onlab.onos.net.flow.FlowRuleBatchEntry.FlowRuleOperation;
import org.onlab.onos.net.flow.FlowRuleBatchOperation;
import org.onlab.onos.net.flow.FlowRuleService;
import org.onlab.onos.net.flow.TrafficSelector;
import org.onlab.onos.net.flow.TrafficTreatment;
import org.onlab.onos.net.intent.IntentExtensionService;
import org.onlab.onos.net.intent.IntentInstaller;
import org.onlab.onos.net.intent.OpticalPathIntent;
import org.onlab.onos.net.resource.DefaultLinkResourceRequest;
import org.onlab.onos.net.resource.Lambda;
import org.onlab.onos.net.resource.LambdaResourceAllocation;
import org.onlab.onos.net.resource.LinkResourceAllocations;
import org.onlab.onos.net.resource.LinkResourceRequest;
import org.onlab.onos.net.resource.LinkResourceService;
import org.onlab.onos.net.resource.ResourceAllocation;
import org.onlab.onos.net.resource.ResourceType;
import org.onlab.onos.net.topology.TopologyService;
import org.slf4j.Logger;

import java.util.List;

import static org.onlab.onos.net.flow.DefaultTrafficTreatment.builder;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * Installer for {@link org.onlab.onos.net.intent.OpticalPathIntent optical path connectivity intents}.
 */
@Component(immediate = true)
public class OpticalPathIntentInstaller implements IntentInstaller<OpticalPathIntent> {
    private final Logger log = getLogger(getClass());

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected IntentExtensionService intentManager;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected FlowRuleService flowRuleService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected CoreService coreService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected TopologyService topologyService;

    @Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
    protected LinkResourceService resourceService;

    private ApplicationId appId;

    //final short WAVELENGTH = 80;
    static final short SIGNAL_TYPE = (short) 1;

    @Activate
    public void activate() {
        appId = coreService.registerApplication("org.onlab.onos.net.intent");
        intentManager.registerInstaller(OpticalPathIntent.class, this);
    }

    @Deactivate
    public void deactivate() {
        intentManager.unregisterInstaller(OpticalPathIntent.class);
    }

    @Override
    public List<FlowRuleBatchOperation> install(OpticalPathIntent intent) {
        LinkResourceAllocations allocations = assignWavelength(intent);
        return generateRules(intent, allocations, FlowRuleOperation.ADD);
    }

    @Override
    public List<FlowRuleBatchOperation> uninstall(OpticalPathIntent intent) {
        LinkResourceAllocations allocations = resourceService.getAllocations(intent.id());
        return generateRules(intent, allocations, FlowRuleOperation.REMOVE);
    }

    @Override
    public List<FlowRuleBatchOperation> replace(OpticalPathIntent intent,
                                                OpticalPathIntent newIntent) {
        // FIXME: implement this
        return null;
    }

    private LinkResourceAllocations assignWavelength(OpticalPathIntent intent) {
        LinkResourceRequest.Builder request = DefaultLinkResourceRequest.builder(intent.id(),
                                                                                 intent.path().links())
                .addLambdaRequest();
        LinkResourceAllocations retLambda = resourceService.requestResources(request.build());
        return retLambda;
    }

    private List<FlowRuleBatchOperation> generateRules(OpticalPathIntent intent,
                                                       LinkResourceAllocations allocations,
                                                       FlowRuleOperation operation) {
        TrafficSelector.Builder selectorBuilder = DefaultTrafficSelector.builder();
        selectorBuilder.matchInport(intent.src().port());

        List<FlowRuleBatchEntry> rules = Lists.newLinkedList();
        ConnectPoint prev = intent.src();

        //TODO throw exception if the lambda was not assigned successfully
        for (Link link : intent.path().links()) {
            Lambda la = null;
            for (ResourceAllocation allocation : allocations.getResourceAllocation(link)) {
                if (allocation.type() == ResourceType.LAMBDA) {
                    la = ((LambdaResourceAllocation) allocation).lambda();
                    break;
                }
            }

            if (la == null) {
                log.info("Lambda was not assigned successfully");
                return null;
            }

            TrafficTreatment.Builder treatmentBuilder = DefaultTrafficTreatment.builder();
            treatmentBuilder.setOutput(link.src().port());
            treatmentBuilder.setLambda((short) la.toInt());

            FlowRule rule = new DefaultFlowRule(prev.deviceId(),
                                                selectorBuilder.build(),
                                                treatmentBuilder.build(),
                                                100,
                                                appId,
                                                100,
                                                true);

            rules.add(new FlowRuleBatchEntry(operation, rule));

            prev = link.dst();
            selectorBuilder.matchInport(link.dst().port());
            selectorBuilder.matchOpticalSignalType(SIGNAL_TYPE); //todo
            selectorBuilder.matchLambda((short) la.toInt());

        }

        // build the last T port rule
        TrafficTreatment.Builder treatmentLast = builder();
        treatmentLast.setOutput(intent.dst().port());
        FlowRule rule = new DefaultFlowRule(intent.dst().deviceId(),
                                            selectorBuilder.build(),
                                            treatmentLast.build(),
                                            100,
                                            appId,
                                            100,
                                            true);
        rules.add(new FlowRuleBatchEntry(operation, rule));

        return Lists.newArrayList(new FlowRuleBatchOperation(rules));
    }

    /*private Lambda assignWavelength(List<Link> links) {
        // TODO More wavelength assignment algorithm
        int wavenum = 0;
        Iterator<Link> itrlink = links.iterator();
        for (int i = 1; i <= WAVELENGTH; i++) {
            wavenum = i;
            boolean found = true;
            while (itrlink.hasNext()) {
                Link link = itrlink.next();
                if (isWavelengthUsed(link, i)) {
                    found = false;
                    break;
                }
            }
            // First-Fit wavelength assignment algorithm
            if (found) {
                break;
            }
        }

        if (wavenum == 0) {
            return null;
        }

        Lambda wave = Lambda.valueOf(wavenum);
        return wave;
    }

    private boolean isWavelengthUsed(Link link, int i) {
        Iterable<LinkResourceAllocations> wave = resourceService.getAllocations(link);
        for (LinkResourceAllocations ir : wave) {
            //if ir.resources().contains(i) {
            //}
        }
        return false;
    }*/
}
