package org.chromium.mojo.system;

import org.chromium.mojo.system.DataPipe.ConsumerHandle;
import org.chromium.mojo.system.DataPipe.ProducerHandle;

public interface UntypedHandle extends Handle {
    UntypedHandle pass();

    ConsumerHandle toDataPipeConsumerHandle();

    ProducerHandle toDataPipeProducerHandle();

    MessagePipeHandle toMessagePipeHandle();

    SharedBufferHandle toSharedBufferHandle();
}
