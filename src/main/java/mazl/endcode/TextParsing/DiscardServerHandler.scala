package mazl.endcode.TextParsing

import io.netty.channel.{ChannelHandlerContext, ChannelInboundHandlerAdapter}

class DiscardServerHandler() extends ChannelInboundHandlerAdapter {
  override def channelRead(ctx: ChannelHandlerContext, msg: scala.Any): Unit = super.channelRead(ctx, msg)
}
