package io.xdag.net.libp2p.discovery;

import io.xdag.utils.SafeFuture;
import org.apache.tuweni.bytes.Bytes;

import java.util.Optional;
import java.util.stream.Stream;

public interface DiscoveryService {

    SafeFuture<?> start();

    SafeFuture<?> stop();

    Stream<DiscoveryPeer> streamKnownPeers();

    SafeFuture<Void> searchForPeers();

    Optional<String> getEnr();

    void updateCustomENRField(String fieldName, Bytes value);
}

