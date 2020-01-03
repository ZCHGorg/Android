package io.charg.chargstation.ui.activities.chargeCoinService;

import io.charg.chargstation.services.remote.api.chargCoinServiceApi.NodeDto;

public class NodeVM {

    private final NodeDto mNodeDto;
    private final String mNodeAddress;

    public NodeVM(NodeDto nodeDto, String nodeAddress) {
        mNodeDto = nodeDto;
        mNodeAddress = nodeAddress;
    }

    public NodeDto getNodeDto() {
        return mNodeDto;
    }

    public String getNodeAddress() {
        return mNodeAddress;
    }
}
