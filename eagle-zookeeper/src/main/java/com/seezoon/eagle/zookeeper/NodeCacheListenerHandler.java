package com.seezoon.eagle.zookeeper;

import org.apache.curator.framework.recipes.cache.NodeCache;

public interface NodeCacheListenerHandler {

	 /**
     * Called when a change has occurred
     */
    public void     nodeChanged(NodeCache cache) throws Exception;
}
