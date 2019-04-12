package mazl.endcode.TextParsing

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.{ChannelInitializer, ChannelOption}
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel

class TextRecieverServer(val host: String, val port: Integer) {

  def initNettyServer(): String = {
    val groupGroup = new NioEventLoopGroup()
    val workGroup = new NioEventLoopGroup()
    try {
      val bootstrap = new ServerBootstrap()
      bootstrap.group(groupGroup, workGroup)
        .channel(classOf[NioServerSocketChannel])
        .childHandler(new ChannelInitializer[SocketChannel]() {
          override def initChannel(c: SocketChannel): Unit = {
            c.pipeline().addLast(new DiscardServerHandler())
          }
        }).option(ChannelOption.SO_BACKLOG, new Integer(128))
        .childOption(ChannelOption.SO_KEEPALIVE, Boolean.box(false))


      val future = bootstrap.bind(this.port).sync()

      future.channel().closeFuture().sync()

      "111"
    } catch {
      case e: Exception => println("server start failed")
        "failed"
    } finally {
      groupGroup.shutdownGracefully()
      workGroup.shutdownGracefully()
    }
  }
}
