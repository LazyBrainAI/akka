/**
 * Copyright (C) 2016-2018 Lightbend Inc. <https://www.lightbend.com>
 */

package akka.actor.typed

import java.util.Optional

/**
 * Envelope that is published on the eventStream for every message that is
 * dropped due to overfull queues.
 */
final case class Dropped(msg: Any, recipient: ActorRef[Nothing]) {
  /** Java API */
  def getRecipient(): ActorRef[Void] = recipient.asInstanceOf[ActorRef[Void]]
}

/**
 * Exception that an actor fails with if it does not handle a Terminated message.
 */
final case class DeathPactException(ref: ActorRef[Nothing]) extends RuntimeException(s"death pact with $ref was triggered") {
  /** Java API */
  def getRef(): ActorRef[Void] = ref.asInstanceOf[ActorRef[Void]]
}

/**
 * Envelope for dead letters.
 */
final case class DeadLetter(msg: Any)

/**
 * System signals are notifications that are generated by the system and
 * delivered to the Actor behavior in a reliable fashion (i.e. they are
 * guaranteed to arrive in contrast to the at-most-once semantics of normal
 * Actor messages).
 */
trait Signal

/**
 * Lifecycle signal that is fired upon restart of the Actor before replacing
 * the behavior with the fresh one (i.e. this signal is received within the
 * behavior that failed). The replacement behavior will receive PreStart as its
 * first signal.
 */
sealed abstract class PreRestart extends Signal
case object PreRestart extends PreRestart {
  def instance: PreRestart = this
}

/**
 * Lifecycle signal that is fired after this actor and all its child actors
 * (transitively) have terminated. The [[Terminated]] signal is only sent to
 * registered watchers after this signal has been processed.
 */
sealed abstract class PostStop extends Signal
// comment copied onto object for better hints in IDEs
/**
 * Lifecycle signal that is fired after this actor and all its child actors
 * (transitively) have terminated. The [[Terminated]] signal is only sent to
 * registered watchers after this signal has been processed.
 */
case object PostStop extends PostStop {
  def instance: PostStop = this
}

/**
 * Lifecycle signal that is fired when an Actor that was watched has terminated.
 * Watching is performed by invoking the
 * [[akka.actor.typed.scaladsl.ActorContext.watch]]. The DeathWatch service is
 * idempotent, meaning that registering twice has the same effect as registering
 * once. Registration does not need to happen before the Actor terminates, a
 * notification is guaranteed to arrive after both registration and termination
 * have occurred. This message is also sent when the watched actor is on a node
 * that has been removed from the cluster when using akka-cluster or has been
 * marked unreachable when using akka-remote directly.
 *
 * @param ref Scala API: the `ActorRef` for the terminated actor
 */
final case class Terminated(ref: ActorRef[Nothing])(failed: Throwable) extends Signal {
  /**
   * Scala API: If the watched actor is a direct child, and was stopped because it failed, this will contain the
   * Exception it failed with, for all other cases it will be `None`.
   */
  def failure: Option[Throwable] = Option(failed)

  /** Java API: The actor that was watched and got terminated */
  def getRef(): ActorRef[Void] = ref.asInstanceOf[ActorRef[Void]]

  /**
   * Java API: If the watched actor is a direct child, and was stopped because it failed, this will contain the
   * Exception it failed with, for all other cases it will be an empty `Optional`.
   */
  def getFailure: Optional[Throwable] = Optional.ofNullable(failed)

}
