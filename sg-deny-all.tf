provider "aws" {
  region = "us-east-1"  # Change this to your desired AWS region
}

resource "aws_security_group" "deny_all_sg" {
  name_prefix = "deny-all-sg-"
  
  // Inbound rules to deny all traffic
  ingress {
    from_port   = 0
    to_port     = 65535
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  // Outbound rules to deny all traffic
  egress {
    from_port   = 0
    to_port     = 65535
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  
  tags = {
    Name = "Deny All SG"
  }
}
