resource "helm_release" "metrics_server" {
  name       = "metrics-server"
  repository = "https://kubernetes-sigs.github.io/metrics-server/"
  chart      = "metrics-server"
  namespace  = "kube-system"
  version    = "3.11.0"  # optional, specify the desired chart version

  set {
    name  = "image.repository"
    value = "k8s.gcr.io/metrics-server/metrics-server"
  }

  set {
    name  = "image.tag"
    value = "v0.7.0"
  }

  set {
    name  = "args[0]"
    value = "--kubelet-insecure-tls"
  }
}
